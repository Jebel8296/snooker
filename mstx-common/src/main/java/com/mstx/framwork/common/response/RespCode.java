package com.mstx.framwork.common.response;

/**
 * 根据业务自行定义相应的业务返回码
 */
public enum RespCode {
    CODE_10200(10200, "succe"),
    CODE_10500(10500, "error"),
    CODE_10501(10501, "系统繁忙 ，稍后再试"),
    CODE_10502(10502, "参数不符合规范"),
    CODE_10503(10503, "签名不符合规范"),
    CODE_10504(10504, "Token已过期"),
    CODE_10505(10505, "服务不存在"),
    CODE_10506(10506, "无权限操作"),
    CODE_10507(10507, "密码有误，请重新输入"),
    CODE_10508(10508, "调用BOSS失败，稍后再试"),
    CODE_20001(20001, "代理商接口调用信息有误"),
    CODE_10509(10509, "短信验证码错误或已失效，请重新输入"),
    CODE_10510(10510, "处理失败，请稍后再试"),
    CODE_10511(10511, "订单不存在或异常"),
    CODE_10512(10512, "暂不支持此支付通道"),
    CODE_10513(10513, "调用支付通道失败，请稍后再试"),
    CODE_10514(10514, "充值订单支付异常，请联系客服"),
    CODE_10515(10515, "充值订单已存在，请勿重复提交"),
    CODE_10516(10516, "前后密码不一致，请重新输入");
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
