package cn.stt.common.enums;

/**
 * @ClassName CodeEnum
 * @Description 编码枚举
 * @Author shitt7
 * @Date 2019/1/11 10:01
 * @Version 1.0
 */
public enum CodeEnum {
    /**
     * 成功编码
     */
    SUCCESS(0, "成功"),
    FAILURE(1, "错误"),
    PARAMETER_ERROR(115, "参数不对"),
    SERRVER_ERROR(500, "服务器错误");


    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
