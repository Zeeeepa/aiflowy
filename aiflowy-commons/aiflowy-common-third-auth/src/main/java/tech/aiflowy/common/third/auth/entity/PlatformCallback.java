package tech.aiflowy.common.third.auth.entity;

public class PlatformCallback {

    private String code;
    private String state;
    private String authCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Override
    public String toString() {
        return "PlatformCallback{" +
                "code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", authCode='" + authCode + '\'' +
                '}';
    }
}
