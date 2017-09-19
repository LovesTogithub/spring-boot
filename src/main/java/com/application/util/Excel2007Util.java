package com.application.util;

import com.application.util.ExcelDataBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.IntStream;

/**
 * 使用 POI 操作 Excel 文件
 * Created by chengchao on 17-5-10.
 */
public class Excel2007Util {
    protected static final Logger logger = LoggerFactory.getLogger(Excel2007Util.class);

    /**
     * 使用 POI 创建一个简单的 Excel 文件
     *
     * @param excelDataBean
     * @param outputStream
     * @throws IOException
     */
    public static void createExcel(final ExcelDataBean excelDataBean,
                                   final OutputStream outputStream) throws IOException {


        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet();

        final List<List<?>> headerList = excelDataBean.getHeaderList();

        IntStream.range(0, headerList.size())
                .forEach(i ->
                        createRow(i, 0, headerList, sheet));

        final int rowOffset = headerList.size();
        final List<List<?>> rowList = excelDataBean.getRowList();

        IntStream.range(0, rowList.size())
                .forEach(i ->
                        createRow(i, rowOffset, rowList, sheet));


        workbook.write(outputStream);

    }

    private static void createRow(final int index, final int rowOffset,
                                  final List<List<?>> rowList,
                                  final Sheet sheet) {
        List<?> data = rowList.get(index);
        if (Objects.nonNull(data) && !data.isEmpty()) {
            final Row row = sheet.createRow(index + rowOffset);
            IntStream.range(0, data.size())
                    .forEach(j ->
                            createCell(j, data, row));
        }
    }

    private static void createCell(final int index, final List<?> data, final Row row) {

        final Object obj = data.get(index);
        final Cell cell = row.createCell(index);
        if (obj == null) {
            cell.setCellValue("");
        } else if (Double.class.isInstance(obj)) {
            cell.setCellValue((double) obj);
        } else if (Date.class.isInstance(obj)) {
            Date date = (Date) obj;
            cell.setCellValue(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toString());
        } else if (Calendar.class.isInstance(obj)) {
            cell.setCellValue((Calendar) obj);
        } else if (Boolean.class.isInstance(obj)) {
            cell.setCellValue((boolean) obj);
        } else {
            final CreationHelper createHelper = row.getSheet().getWorkbook().getCreationHelper();
            cell.setCellValue(createHelper.createRichTextString(obj.toString()));
        }
    }

    /**
     * 遍历目录下所有excel并生成list
     *
     * @param  reportFile
     * @return
     */
    public static List importExcel2Sql(MultipartFile reportFile) {

        try {
            //下面的注释其实是想批量执行所传目录的文件列表的
            //但是我忽略了我是在本地测的
//            File file = new File(filePath);
//            String[] list = file.list();
//            for (int i = 0; i < list.length; i++) {
//                String filePath1 = filePath + "/" + list[i];
            InputStream is = reportFile.getInputStream();
            Workbook hssfWorkbook = null;
            String fileName = reportFile.getOriginalFilename();
            if (isExcel2003(fileName)) {
                //如果是2003就用HSSFWorkbook
                hssfWorkbook = new HSSFWorkbook(is);
            } else {
                //如果是其他就用HSSFWorkbook
                hssfWorkbook = new XSSFWorkbook(is);
            }
            // 循环工作表Sheet
            List listResult = new ArrayList();
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                // 循环行Row
                //从1开始，忽略标题行
                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    StringBuffer sb = new StringBuffer();
                    Row hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow != null) {
                        //为了数据完整，获取第一行的字段的长度防止下边的获取getLastCellNum个数不够
                        int cellLastNum = hssfSheet.getRow(0).getLastCellNum();
                        for (int cellNum = 0; cellNum < cellLastNum; cellNum++) {
                            Cell cell = hssfRow.getCell(cellNum);
                            if (cell == null) {
                                sb.append("NULL");
                            } else if (isNum(cell.toString())) {
                                sb.append(cell);
                            } else {
                                sb.append("'").append(cell).append("'");
                            }
                            if (cellNum < cellLastNum - 1) {
                                sb.append(",");
                            }
                        }
                        listResult.add(sb);
                    }
                }
            }
            return listResult;
//            }
        } catch (Exception e) {
            logger.error("Exception:{}", e);
        }
        return null;
    }

    /**
     * 根据文件路径文件名判断是否是2003版本
     *
     * @param filePath
     * @return
     */
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 判断一个字符串是否能转化为数字方法
     *
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());

        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());

        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());

        }

    }
}
