package tech.aiflowy.common.third.auth.entity;

public class PlatformConfig {

    /**
     * 第三方平台 appId
     */
    private String appId;
    /**
     * 第三方平台 appSecret
     */
    private String appSecret;
    /**
     * 回调地址
     */
    private String redirectUri;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
