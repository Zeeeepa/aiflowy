package tech.aiflowy.common.third.auth.core;

import tech.aiflowy.common.third.auth.entity.AccessToken;
import tech.aiflowy.common.third.auth.entity.PlatformCallback;
import tech.aiflowy.common.third.auth.entity.PlatformUser;

public interface AuthRequest {

    /**
     * 获取授权地址
     *
     * @return 授权地址
     */
    String getAuthUrl(String state);

    /**
     * 获取 AccessToken
     *
     * @param callback 回调参数
     * @return AccessToken
     */
    AccessToken getAccessToken(PlatformCallback callback);

    /**
     * 获取用户信息
     *
     * @param accessToken AccessToken
     * @return 用户信息
     */
    PlatformUser getUserInfo(AccessToken accessToken);
}
