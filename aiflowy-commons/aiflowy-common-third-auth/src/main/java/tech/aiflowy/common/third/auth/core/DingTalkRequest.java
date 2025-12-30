package tech.aiflowy.common.third.auth.core;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.aiflowy.common.third.auth.entity.AccessToken;
import tech.aiflowy.common.third.auth.entity.PlatformCallback;
import tech.aiflowy.common.third.auth.entity.PlatformConfig;
import tech.aiflowy.common.third.auth.entity.PlatformUser;

public class DingTalkRequest implements AuthRequest {

    public static final String AUTH_URL = "https://login.dingtalk.com/oauth2/auth";
    public static final String TOKEN_URL = "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
    public static final String USER_INFO_URL = "https://api.dingtalk.com/v1.0/contact/users/me";
    private static final Logger log = LoggerFactory.getLogger(DingTalkRequest.class);

    private final PlatformConfig config;

    public DingTalkRequest(PlatformConfig config) {
        this.config = config;
    }

    @Override
    public String getAuthUrl(String state) {
        String appId = config.getAppId();
        String redirectUri = config.getRedirectUri();
        return UrlBuilder.of(AUTH_URL)
                .addQuery("response_type", "code")
                .addQuery("client_id", appId)
                .addQuery("scope", "openid")
                .addQuery("redirect_uri", redirectUri)
                .addQuery("prompt", "consent")
                .addQuery("state", state)
                .build();
    }

    @Override
    public AccessToken getAccessToken(PlatformCallback callback) {
        JSONObject body = new JSONObject();
        body.put("grantType", "authorization_code");
        body.put("clientId", config.getAppId());
        body.put("clientSecret", config.getAppSecret());
        body.put("code", callback.getAuthCode());
        HttpRequest post = HttpUtil.createPost(TOKEN_URL);
        post.body(body.toJSONString());
        post.header("Content-Type", "application/json");
        AccessToken accessToken = new AccessToken();
        try (HttpResponse execute = post.execute()) {
            JSONObject json = JSON.parseObject(execute.body());
            log.info("ding-talk access token: {}", json);
            if (json.containsKey("errcode")) {
                throw new RuntimeException(json.getString("errmsg"));
            }
            accessToken.setAccessToken(json.getString("accessToken"));
            accessToken.setExpiresIn(json.getInteger("expireIn"));
            accessToken.setRefreshToken(json.getString("refreshToken"));
            accessToken.setCorpId(json.getString("corpId"));
        }
        return accessToken;
    }

    @Override
    public PlatformUser getUserInfo(AccessToken accessToken) {
        PlatformUser user = new PlatformUser();
        HttpRequest get = HttpUtil.createGet(USER_INFO_URL);
        get.header("x-acs-dingtalk-access-token", accessToken.getAccessToken());
        try (HttpResponse execute = get.execute()) {
            JSONObject json = JSON.parseObject(execute.body());
            log.info("ding-talk user info: {}", json);
            if (json.containsKey("errcode")) {
                throw new RuntimeException(json.getString("errmsg"));
            }
            user.setUserKey(json.getString("unionId"));
            user.setUsername(json.getString("unionId"));
            user.setNickname(json.getString("nick"));
            user.setAvatar(json.getString("avatarUrl"));
            user.setOrigData(json);
        }
        return user;
    }
}
