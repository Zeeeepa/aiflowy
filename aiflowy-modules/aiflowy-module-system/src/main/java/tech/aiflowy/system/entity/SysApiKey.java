package tech.aiflowy.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.system.entity.base.SysApiKeyBase;

import java.math.BigInteger;
import java.util.List;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-18
 */
@Table("tb_sys_api_key")
public class SysApiKey extends SysApiKeyBase {

    @Column(ignore = true)
    List<BigInteger> permissionIds;

    @RelationOneToMany(selfField = "id", targetField = "apiKeyId", targetTable = "tb_sys_api_key_resource_mapping")
    private List<SysApiKeyResourcePermissionRelationship> resourcePermissions;

    public List<SysApiKeyResourcePermissionRelationship> getResourcePermissions() {
        return resourcePermissions;
    }

    public void setResourcePermissions(List<SysApiKeyResourcePermissionRelationship> resourcePermissions) {
        this.resourcePermissions = resourcePermissions;
    }

    public List<BigInteger> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<BigInteger> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
