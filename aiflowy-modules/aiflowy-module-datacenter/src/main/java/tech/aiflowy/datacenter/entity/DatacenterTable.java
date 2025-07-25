package tech.aiflowy.datacenter.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.datacenter.entity.base.DatacenterTableBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据中枢表 实体类。
 *
 * @author ArkLight
 * @since 2025-07-10
 */
@Table(value = "tb_datacenter_table", comment = "数据中枢表")
public class DatacenterTable extends DatacenterTableBase {

    @Column(ignore = true)
    private List<DatacenterTableFields> fields = new ArrayList<>();

    public List<DatacenterTableFields> getFields() {
        return fields;
    }

    public void setFields(List<DatacenterTableFields> fields) {
        this.fields = fields;
    }
}
