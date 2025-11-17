package tech.aiflowy.common.vo;

public class PkVo {

    public PkVo(Object[] id) {
        this.id = id;
    }

    private Object[] id;

    public Object[] getId() {
        return id;
    }

    public void setId(Object[] id) {
        this.id = id;
    }
}
