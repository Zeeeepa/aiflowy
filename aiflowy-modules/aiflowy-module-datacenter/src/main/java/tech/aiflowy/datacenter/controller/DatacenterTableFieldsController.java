package tech.aiflowy.datacenter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.service.DatacenterTableFieldsService;

import java.util.Date;

/**
 * 控制层。
 *
 * @author ArkLight
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/api/v1/datacenterTableFields")
@UsePermission(moduleName = "/api/v1/datacenterTable")
public class DatacenterTableFieldsController extends BaseCurdController<DatacenterTableFieldsService, DatacenterTableFields> {

    public DatacenterTableFieldsController(DatacenterTableFieldsService service) {
        super(service);
    }

    @Override
    protected Result onSaveOrUpdateBefore(DatacenterTableFields entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        return null;
    }
}