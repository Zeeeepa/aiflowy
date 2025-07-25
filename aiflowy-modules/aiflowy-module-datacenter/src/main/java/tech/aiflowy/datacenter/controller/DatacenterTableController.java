package tech.aiflowy.datacenter.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import org.springframework.web.bind.annotation.*;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.entity.DatacenterQuery;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.datacenter.entity.DatacenterTable;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.service.DatacenterTableFieldsService;
import tech.aiflowy.datacenter.service.DatacenterTableService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据中枢表 控制层。
 *
 * @author ArkLight
 * @since 2025-07-10
 */
@RestController
@RequestMapping("/api/v1/datacenterTable")
public class DatacenterTableController extends BaseCurdController<DatacenterTableService, DatacenterTable> {

    @Resource
    private DatacenterTableFieldsService fieldsService;

    public DatacenterTableController(DatacenterTableService service) {
        super(service);
    }

    @Override
    protected Result onSaveOrUpdateBefore(DatacenterTable entity, boolean isSave) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        if (isSave) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModifiedBy(loginUser.getId());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @PostMapping("/saveTable")
    @SaCheckPermission("/api/v1/datacenterTable/save")
    public Result saveTable(@RequestBody DatacenterTable entity) {
        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
        List<DatacenterTableFields> fields = entity.getFields();
        if (CollectionUtil.isEmpty(fields)) {
            return Result.fail(99, "字段不能为空");
        }
        BigInteger id = entity.getId();
        if (id == null) {
            commonFiled(entity, loginUser.getId(), loginUser.getTenantId(), loginUser.getDeptId());
        } else {
            entity.setModified(new Date());
            entity.setModifiedBy(loginUser.getId());
        }
        service.saveTable(entity, loginUser);
        return Result.success();
    }

    @GetMapping("/detailInfo")
    @SaCheckPermission("/api/v1/datacenterTable/query")
    public Result detailInfo(BigInteger tableId) {
        DatacenterTable table = service.getById(tableId);
        QueryWrapper wrapper = QueryWrapper.create();
        wrapper.eq(DatacenterTableFields::getTableId, tableId);
        wrapper.orderBy("id");
        List<DatacenterTableFields> fields = fieldsService.list(wrapper);
        table.setFields(fields);
        return Result.success(table);
    }

    @GetMapping("/removeTable")
    @SaCheckPermission("/api/v1/datacenterTable/remove")
    public Result removeTable(BigInteger tableId) {
        service.removeTable(tableId);
        return Result.success();
    }

    @GetMapping("/getHeaders")
    @SaCheckPermission("/api/v1/datacenterTable/query")
    public Result getHeaders(BigInteger tableId) {
        List<JSONObject> res = service.getHeaders(tableId);
        return Result.success(res);
    }

    @GetMapping("/getPageData")
    @SaCheckPermission("/api/v1/datacenterTable/query")
    public Result getPageData(DatacenterQuery where) {
        Page<Row> res = service.getPageData(where);
        return Result.success(res);
    }

    @PostMapping("/saveValue")
    @SaCheckPermission("/api/v1/datacenterTable/save")
    public Result saveValue(@RequestParam Map<String, Object> map) {
        JSONObject object = new JSONObject(map);
        BigInteger tableId = object.getBigInteger("tableId");
        LoginAccount account = SaTokenUtil.getLoginAccount();
        if (tableId == null) {
            return Result.fail(99, "参数错误");
        }
        service.saveValue(tableId, object, account);
        return Result.success();
    }

    @PostMapping("/removeValue")
    @SaCheckPermission("/api/v1/datacenterTable/remove")
    public Result removeValue(@RequestParam Map<String, Object> map) {
        JSONObject object = new JSONObject(map);
        BigInteger tableId = object.getBigInteger("tableId");
        BigInteger id = object.getBigInteger("id");
        if (tableId == null || id == null) {
            return Result.fail(99, "参数错误");
        }
        LoginAccount account = SaTokenUtil.getLoginAccount();
        service.removeValue(tableId, id, account);
        return Result.success();
    }
}