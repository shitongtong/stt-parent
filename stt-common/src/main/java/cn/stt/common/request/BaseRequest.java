package cn.stt.common.request;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/18.
 */
public class BaseRequest {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
