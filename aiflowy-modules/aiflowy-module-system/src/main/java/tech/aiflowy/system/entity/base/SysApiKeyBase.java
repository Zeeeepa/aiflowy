package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;


public class SysApiKeyBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private Long id;

    /**
     * apiKey
     */
    @Column(comment = "apiKey")
    private String apiKey;

    /**
     * 部门id
     */
    @Column(comment = "deptId")
    private BigInteger deptId;

    /**
     * 租户id
     */
    @Column(comment = "tenantId")
    private BigInteger tenantId;

    /**
     * 创建人
     */
    @Column(comment = "userId")
    private BigInteger createdBy;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private Date created;

    /**
     * 失效时间
     */
    @Column(comment = "失效时间")
    private Date expiredAt;

    /**
     * 状态1启用 2失效
     */
    @Column(comment = "状态1启用 2失效")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigInteger getDeptId() {
        return deptId;
    }

    public void setDeptId(BigInteger deptId) {
        this.deptId = deptId;
    }

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getCreatedBy() {return createdBy;}

    public void setCreatedBy(BigInteger createdBy) {this.createdBy = createdBy;}

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }
}
