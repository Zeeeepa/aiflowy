package tech.aiflowy.datacenter.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.datacenter.entity.base.DatacenterTableFieldsBase;

import java.math.BigInteger;


/**
 *  实体类。
 *
 * @author ArkLight
 * @since 2025-07-10
 */
@Table(value = "tb_datacenter_table_fields", comment = "数据中枢字段表")
public class DatacenterTableFields extends DatacenterTableFieldsBase {

    /**
     * 是否删除该字段
     * 前端传入 true 则删除该字段
     */
    @Column(ignore = true)
    private Boolean handleDelete = false;

    public Boolean isHandleDelete() {
        return handleDelete;
    }

    public void setHandleDelete(Boolean handleDelete) {
        this.handleDelete = handleDelete;
    }

    /**
     * 前端区分 rowKey
     */
    public BigInteger getRowKey() {
        return this.getId();
    }
}
