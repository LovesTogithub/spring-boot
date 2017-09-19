package com.application.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Excel007Util 工具类创建 Excel 2007 文件所需要的数据类.
 * 包含多个包含标题信息的头列表
 * 和多个内容行列表.
 * 超出 Excel 文件行列限制时会抛出异常, 不过没测试过这么大的数据.
 *
 * Created by chengchao on 17-5-10.
 */
public class ExcelDataBean {

    /**
     * 2007的是1048576行、16384列.
     */
    public static final int MAX_ROW_SIZE = 1048576;
    public static final int MAX_COL_SIZE = 16384;

    private List<List<?>> headerList;
    private List<List<?>> rowList;

    public List<List<?>> getHeaderList() {
        return headerList;
    }

    public List<List<?>> getRowList() {
        return rowList;
    }

    private ExcelDataBean(List<List<?>> headerList, List<List<?>> rowList) {
        this.headerList = headerList;
        this.rowList = rowList;
    }

    public static class Builder {
        private List<List<?>> headerList;
        private List<List<?>> rowList;

        public Builder() {
            headerList = new ArrayList<>();
            rowList = new ArrayList<>();
        }

        public Builder addHeaderList(List<?> header) {
            if (header.size() > MAX_COL_SIZE) {
                throw new RuntimeException("数据列超出 Excel 允许的最大值!");
            }
            if (headerList.size() + rowList.size() + 1 > MAX_ROW_SIZE) {
                throw new RuntimeException("数据行超出 Excel 允许的最大值!");
            }
            this.headerList.add(header);
            return this;
        }

        public Builder addRowList(List<?> row) {
            if (row.size() > MAX_COL_SIZE) {
                throw new RuntimeException("数据列超出 Excel 允许的最大值!");
            }
            if (headerList.size() + rowList.size() + 1 > MAX_ROW_SIZE) {
                throw new RuntimeException("数据行超出 Excel 允许的最大值!");
            }

            this.rowList.add(row);
            return this;
        }

        public ExcelDataBean build() {
            return new ExcelDataBean(headerList, rowList);
        }
    }
}
