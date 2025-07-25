package tech.aiflowy.common.entity;

import java.math.BigInteger;

public class DatacenterQuery {

    private Long pageNumber;
    private Long pageSize;
    // 表ID
    private BigInteger tableId;
    // 工作流传过来的查询条件
    private String where;

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public BigInteger getTableId() {
        return tableId;
    }

    public void setTableId(BigInteger tableId) {
        this.tableId = tableId;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
