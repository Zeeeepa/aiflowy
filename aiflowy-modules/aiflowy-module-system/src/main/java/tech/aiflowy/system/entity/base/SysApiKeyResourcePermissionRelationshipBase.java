package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;


public class SysApiKeyResourcePermissionRelationshipBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private BigInteger id;

    /**
     * api_key_id
     */
    @Column(comment = "api_key_id")
    private BigInteger apiKeyId;

    /**
     * 请求接口资源访问id
     */
    @Column(comment = "请求接口资源访问id")
    private BigInteger apiKeyResourceId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getApiKeyId() {
        return apiKeyId;
    }

    public void setApiKeyId(BigInteger apiKeyId) {
        this.apiKeyId = apiKeyId;
    }

    public BigInteger getApiKeyResourceId() {
        return apiKeyResourceId;
    }

    public void setApiKeyResourceId(BigInteger apiKeyResourceId) {
        this.apiKeyResourceId = apiKeyResourceId;
    }

}
