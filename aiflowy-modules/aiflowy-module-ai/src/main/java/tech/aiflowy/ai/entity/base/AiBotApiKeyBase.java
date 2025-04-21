package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

import java.io.Serializable;
import java.time.LocalDateTime;


public class AiBotApiKeyBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = "snowFlakeId", comment = "id")
    private Long id;

    /**
     * apiKey
     */
    @Id(comment = "apiKey")@Column(value = "apiKey", comment = "apiKey")
    private String apiKey;

    /**
     * 创建时间
     */
    @Column(comment = "创建时间")
    private LocalDateTime created;

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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
