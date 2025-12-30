package tech.aiflowy.admin.controller.auth;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alicp.jetcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.constant.CacheKey;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.third.auth.core.AuthRequest;
import tech.aiflowy.common.third.auth.core.DingTalkRequest;
import tech.aiflowy.common.third.auth.core.WechatWebRequest;
import tech.aiflowy.common.third.auth.entity.AccessToken;
import tech.aiflowy.common.third.auth.entity.PlatformCallback;
import tech.aiflowy.common.third.auth.entity.PlatformConfig;
import tech.aiflowy.common.third.auth.entity.PlatformUser;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.entity.SysOption;
import tech.aiflowy.system.service.SysAccountService;
import tech.aiflowy.system.service.SysOptionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/thirdAuth")
public class ThirdAuthController {

    private static final Logger log = LoggerFactory.getLogger(ThirdAuthController.class);
    @Resource
    private SysOptionService sysOptionService;
    @Resource(name = "defaultCache")
    private Cache<String, Object> defaultCache;
    @Value("${aiflowy.admin-page-domain}")
    public String adminPageDomain;
    @Resource
    private SysAccountService accountService;

    /**
     * 获取登录链接
     */
    @GetMapping("/getAuthUrl")
    public Result<String> wechatWeb(String platform) {
        AuthRequest request = getRequest(platform);
        String state = IdUtil.fastSimpleUUID();
        defaultCache.put(CacheKey.OAUTH_STATE_KEY, state, 10, TimeUnit.MINUTES);
        String authUrl = request.getAuthUrl(state);
        return Result.ok(authUrl);
    }

    /**
     * 回调-登录-重定向前端
     */
    @GetMapping("/callback/{platform}")
    public void callback(@PathVariable String platform, PlatformCallback callback, HttpServletResponse response) throws Exception {
        Object cache = defaultCache.get(CacheKey.OAUTH_STATE_KEY);
        if (cache == null) {
            log.error("【{}】 ---> 无效授权：{}", platform, callback);
            throw new BusinessException("无效的授权");
        }
        defaultCache.remove(CacheKey.OAUTH_STATE_KEY);
        AuthRequest request = getRequest(platform);
        AccessToken accessToken = request.getAccessToken(callback);
        PlatformUser userInfo = request.getUserInfo(accessToken);
        String userKey = userInfo.getUserKey();
        SysAccount record = accountService.getByUsername(userKey);
        if (record == null) {
            record = accountService.savePlatformUser(platform, userInfo);
        }
        StpUtil.login(record.getId());
        StpUtil.getSession().set(Constants.LOGIN_USER_KEY, record.toLoginAccount());
        String tokenValue = StpUtil.getTokenValue();
        response.sendRedirect(adminPageDomain + "/oauth?token=" + tokenValue);
    }

    private AuthRequest getRequest(String platform) {
        if (StrUtil.isEmpty(platform)) {
            throw new BusinessException("未指定平台");
        }
        SysOption option = sysOptionService.getByOptionKey(platform);
        if (option == null) {
            throw new BusinessException("未配置参数");
        }
        PlatformConfig config = JSON.parseObject(option.getValue(), PlatformConfig.class);
        AuthRequest res;
        switch (platform) {
            case "wx_web":
                res = new WechatWebRequest(config);
                break;
            case "ding_talk":
                res = new DingTalkRequest(config);
                break;
            default:
                res = null;
        }
        if (res == null) {
            throw new BusinessException("未知平台");
        }
        return res;
    }
}
