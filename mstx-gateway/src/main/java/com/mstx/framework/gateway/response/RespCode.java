package com.mstx.framework.gateway.response;

public enum RespCode {
    CODE_10200(10200, "OK"),
    CODE_10201(10201, "参数不符合规范"),
    CODE_10202(10202, "登陆失效，请重新登陆"),
    CODE_10203(10203, "验证码输入错误，请重新输入"),
    CODE_10500(10500, "网络异常，稍后再试");

    private Integer code;
    private String msg;

    RespCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return String.valueOf(code);
    }
}
