package com.application.util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chengchao on 2016/10/26.
 */
public class ListResult implements Serializable {

    private Integer total;
    private List<?> rows;

    public ListResult(Integer total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }


    public List<?> getRows() {
        return rows;
    }
}
