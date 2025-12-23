package tech.aiflowy.ai.entity.base;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.handler.FastjsonTypeHandler;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Map;


public class ModelBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    private BigInteger id;

    /**
     * 部门ID
     */
    @Column(comment = "部门ID")
    private BigInteger deptId;

    /**
     * 租户ID
     */
    @Column(comment = "租户ID", tenantId = true)
    private BigInteger tenantId;

    /**
     * 标题或名称
     */
    @Column(comment = "标题或名称")
    private String title;

    /**
     * 品牌
     */
    @Column(comment = "品牌")
    private String brand;

    /**
     * ICON
     */
    @Column(comment = "ICON")
    private String icon;

    /**
     * 描述
     */
    @Column(comment = "描述")
    private String description;


    /**
     * 大模型请求地址
     */
    @Column(comment = "大模型请求地址")
    private String llmEndpoint;

    /**
     * 大模型名称
     */
    @Column(comment = "大模型名称")
    private String llmModel;

    /**
     * 大模型 API KEY
     */
    @Column(comment = "大模型 API KEY")
    private String llmApiKey;

    /**
     * 大模型其他属性配置
     */
    @Column(comment = "大模型其他属性配置")
    private String llmExtraConfig;

    /**
     * provider
     */
    @Column(comment = "provider")
    private String provider;

    /**
     * providerId
     */
    @Column(comment = "providerId")
    private BigInteger providerId;

    /**
     * groupName
     */
    @Column(comment = "groupName")
    private String groupName;

    /**
     * modelType
     */
    @Column(comment = "modelType")
    private String modelType;

    /**
     * 其他配置内容
     */
    @Column(typeHandler = FastjsonTypeHandler.class, comment = "其他配置内容")
    private Map<String, Object> options;

    /**
     * added 是否已添加
     */
    @Column(comment = "added")
    private Boolean added;

    /**
     * canDelete 是否能删除，系统添加的默认不允许删除
     */
    @Column(comment = "canDelete")
    private Boolean canDelete;

    /**
     * supportReasoning 是否支持推理
     */
    @Column(comment = "supportReasoning")
    private Boolean supportReasoning;

    /**
     * supportTool 是否支持工具
     */
    @Column(comment = "supportTool")
    private Boolean supportTool;

    /**
     * supportEmbedding 是否支持向量化
     */
    @Column(comment = "supportEmbedding")
    private Boolean supportEmbedding;

    /**
     * supportRerank 是否支持重排
     */
    @Column(comment = "supportRerank")
    private Boolean supportRerank;

    /**
     * supportFree 是否消耗免费
     */
    @Column(comment = "supportFree")
    private Boolean supportFree;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLlmEndpoint() {
        return llmEndpoint;
    }

    public void setLlmEndpoint(String llmEndpoint) {
        this.llmEndpoint = llmEndpoint;
    }

    public String getLlmModel() {
        return llmModel;
    }

    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }

    public String getLlmApiKey() {
        return llmApiKey;
    }

    public void setLlmApiKey(String llmApiKey) {
        this.llmApiKey = llmApiKey;
    }

    public String getLlmExtraConfig() {
        return llmExtraConfig;
    }

    public void setLlmExtraConfig(String  llmExtraConfig) {
        this.llmExtraConfig = llmExtraConfig;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public String getModelType() {return modelType;}

    public void setModelType(String modelType) {this.modelType = modelType;}

    public String getGroupName() {return groupName;}

    public void setGroupName(String groupName) {this.groupName = groupName;}

    public String getProvider() {return provider;}

    public void setProvider(String provider) {this.provider = provider;}

    public BigInteger getProviderId() {return providerId;}

    public void setProviderId(BigInteger providerId) {this.providerId = providerId;}

    public Boolean getAdded() {return added;}

    public void setAdded(Boolean added) {this.added = added;}

    public Boolean getCanDelete() {return canDelete;}

    public void setCanDelete(Boolean canDelete) {this.canDelete = canDelete;}

    public Boolean getSupportReasoning() {return supportReasoning;}

    public void setSupportReasoning(Boolean supportReasoning) {this.supportReasoning = supportReasoning;}

    public Boolean getSupportTool() {return supportTool;}

    public void setSupportTool(Boolean supportTool) {this.supportTool = supportTool;}

    public Boolean getSupportEmbedding() {return supportEmbedding;}

    public void setSupportEmbedding(Boolean supportEmbedding) {this.supportEmbedding = supportEmbedding;}

    public Boolean getSupportRerank() {return supportRerank;}

    public void setSupportRerank(Boolean supportRerank) {this.supportRerank = supportRerank;}

    public Boolean getSupportFree() {return supportFree;}

    public void setSupportFree(Boolean supportFree) {this.supportFree = supportFree;}

    @Override
    public String toString() {
        return "AiLlmBase{" +
                "id=" + id +
                ", deptId=" + deptId +
                ", tenantId=" + tenantId +
                ", title='" + title + '\'' +
                ", brand='" + brand + '\'' +
                ", icon='" + icon + '\'' +
                ", description='" + description + '\'' +
                ", llmEndpoint='" + llmEndpoint + '\'' +
                ", llmModel='" + llmModel + '\'' +
                ", llmApiKey='" + llmApiKey + '\'' +
                ", llmExtraConfig='" + llmExtraConfig + '\'' +
                ", provider='" + provider + '\'' +
                ", providerId=" + providerId +
                ", groupName='" + groupName + '\'' +
                ", modelType='" + modelType + '\'' +
                ", options=" + options +
                ", added=" + added +
                ", canDelete=" + canDelete +
                ", supportReasoning=" + supportReasoning +
                ", supportTool=" + supportTool +
                ", supportEmbedding=" + supportEmbedding +
                ", supportRerank=" + supportRerank +
                ", supportFree=" + supportFree +
                '}';
    }
}
