package tech.aiflowy.starter;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.constant.enums.EnumMenuType;
import tech.aiflowy.system.entity.SysMenu;
import tech.aiflowy.system.entity.SysRoleMenu;
import tech.aiflowy.system.service.SysMenuService;
import tech.aiflowy.system.service.SysRoleMenuService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/test")
public class RouteInfoController {

    @Resource
    private SysMenuService sysMenuService;
    @Resource
    private SysRoleMenuService sysRoleMenuService;

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public RouteInfoController(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    public static void main(String[] args) {

    }

    @GetMapping("/api/routes")
    public Set<String> getAllRoutes() {

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        Set<String> result = new HashSet<>();

        for (RequestMappingInfo mappingInfo : handlerMethods.keySet()) {
            PathPatternsRequestCondition condition = mappingInfo.getPathPatternsCondition();
            Set<PathPattern> patterns = condition.getPatterns();
            for (PathPattern pattern : patterns) {
                result.add(pattern.getPatternString());
            }
        }
        return result;
    }

    @GetMapping("/api/testsave")
    public String testsave() {
        List<String> strings = FileUtil.readLines("D:\\work\\xmkj\\aiflowy\\权限菜单.txt", StandardCharsets.UTF_8);
        for (String string : strings) {
            String[] split = string.split("\\|");
            String query = split[0] + "/query";
            String save = split[0] + "/save";
            String remove = split[0] + "/remove";
            String parentId = split[1];
            SysMenu sysMenu = new SysMenu();
            sysMenu.setParentId(new BigInteger(parentId));
            sysMenu.setMenuType(EnumMenuType.BTN.getCode());
//            sysMenu.setMenuUrl();
//            sysMenu.setComponent();
//            sysMenu.setMenuIcon();
            sysMenu.setIsShow(0);
            sysMenu.setSortNo(1);
            sysMenu.setStatus(0);
            sysMenu.setCreated(new Date());
            sysMenu.setCreatedBy(new BigInteger("1"));
            sysMenu.setModified(new Date());
            sysMenu.setModifiedBy(new BigInteger("1"));
            sysMenu.setRemark("gen");
            sysMenu.setIsDeleted(0);

            sysMenu.setMenuTitle("查询");
            sysMenu.setPermissionTag(query);
            sysMenuService.save(sysMenu);
            saveAdmin(sysMenu.getId());

            sysMenu.setId(null);
            sysMenu.setMenuTitle("保存");
            sysMenu.setPermissionTag(save);
            sysMenuService.save(sysMenu);
            saveAdmin(sysMenu.getId());

            sysMenu.setId(null);
            sysMenu.setMenuTitle("删除");
            sysMenu.setPermissionTag(remove);
            sysMenuService.save(sysMenu);
            saveAdmin(sysMenu.getId());

        }
        return "success";
    }

    private void saveAdmin(BigInteger menuId) {
        SysRoleMenu admin = new SysRoleMenu();
        admin.setRoleId(Constants.SUPER_ADMIN_ROLE_ID);
        admin.setMenuId(menuId);
        sysRoleMenuService.save(admin);
    }
}
