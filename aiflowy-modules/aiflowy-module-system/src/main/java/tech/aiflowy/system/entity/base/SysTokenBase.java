package tech.aiflowy.system.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;


public class SysTokenBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id(keyType = KeyType.Auto, value = "snowFlakeId", comment = "主键")
    private BigInteger id;

    /**
     * 生成的 token 值
     */
    @Column(comment = "生成的 token 值")
    private String token;

    /**
     * 关联用户ID
     */
    @Column(comment = "关联用户ID")
    private Long userId;

    /**
     * 过期时间
     */
    @Column(comment = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private LocalDateTime createdAt;

    /**
     * Token 描述（可选）
     */
    @Column(comment = "Token 描述（可选）")
    private String description;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
