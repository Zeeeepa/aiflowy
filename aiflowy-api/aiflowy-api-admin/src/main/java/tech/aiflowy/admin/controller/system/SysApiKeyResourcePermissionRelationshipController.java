package tech.aiflowy.admin.controller.system;

import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.system.entity.SysApiKeyResourcePermissionRelationship;
import tech.aiflowy.system.service.SysApiKeyResourcePermissionRelationshipService;

/**
 * apikey-请求接口表 控制层。
 *
 * @author 12076
 * @since 2025-12-01
 */
@RestController
@RequestMapping("/api/v1/sysApiKeyResourcePermissionRelationship")
@UsePermission(moduleName = "/api/v1/sysApiKey")
public class SysApiKeyResourcePermissionRelationshipController extends BaseCurdController<SysApiKeyResourcePermissionRelationshipService, SysApiKeyResourcePermissionRelationship> {
    public SysApiKeyResourcePermissionRelationshipController(SysApiKeyResourcePermissionRelationshipService service) {
        super(service);
    }
}