package tech.aiflowy.common.third.auth.core;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.common.third.auth.entity.AccessToken;
import tech.aiflowy.common.third.auth.entity.PlatformCallback;
import tech.aiflowy.common.third.auth.entity.PlatformConfig;
import tech.aiflowy.common.third.auth.entity.PlatformUser;

/**
 * 微信网页应用 授权
 */
public class WechatWebRequest implements AuthRequest {

    public static final String AUTH_URL = "https://open.weixin.qq.com/connect/qrconnect";
    public static final String TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    public static final String USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    private static final Logger log = LoggerFactory.getLogger(WechatWebRequest.class);

    private final PlatformConfig config;

    public WechatWebRequest(PlatformConfig config) {
        this.config = config;
    }

    @Override
    public String getAuthUrl(String state) {
        String appId = config.getAppId();
        String redirectUri = config.getRedirectUri();
        return UrlBuilder.of(AUTH_URL)
                .addQuery("appid", appId)
                .addQuery("redirect_uri", redirectUri)
                .addQuery("response_type", "code")
                .addQuery("scope", "snsapi_login")
                .addQuery("state", state)
                .build();
    }

    @Override
    public AccessToken getAccessToken(PlatformCallback callback) {

        String appId = config.getAppId();
        String appSecret = config.getAppSecret();
        String code = callback.getCode();
        String url = UrlBuilder.of(TOKEN_URL)
                .addQuery("appid", appId)
                .addQuery("secret", appSecret)
                .addQuery("code", code)
                .addQuery("grant_type", "authorization_code")
                .build();

        String res = HttpUtil.get(url);
        JSONObject body = JSON.parseObject(res);
        log.info("wechat-web accessToken: {}", res);
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(body.getString("access_token"));
        accessToken.setExpiresIn(body.getIntValue("expires_in"));
        accessToken.setRefreshToken(body.getString("refresh_token"));
        accessToken.setOpenId(body.getString("openid"));
        accessToken.setScope(body.getString("scope"));
        accessToken.setUnionId(body.getString("unionid"));
        return accessToken;
    }

    @Override
    public PlatformUser getUserInfo(AccessToken accessToken) {
        String url = UrlBuilder.of(USER_INFO_URL)
                .addQuery("access_token", accessToken.getAccessToken())
                .addQuery("openid", accessToken.getOpenId())
                .build();
        String res = HttpUtil.get(url);
        JSONObject body = JSON.parseObject(res);
        log.info("wechat-web userInfo: {}", res);
        PlatformUser user = new PlatformUser();
        user.setUserKey(body.getString("openid"));
        user.setUsername(body.getString("openid"));
        user.setNickname(body.getString("nickname"));
        user.setAvatar(body.getString("headimgurl"));
        user.setOrigData(body);
        return user;
    }
}
