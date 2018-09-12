package com.mstx.framework.gateway.response;

public class RespBody {

    private int code;
    private String msg;
    private Object data;

    public RespBody() {
        this(RespCode.CODE_10200.getCode(), RespCode.CODE_10200.getMsg(), null);
    }

    public RespBody(Object data) {
        this(RespCode.CODE_10200.getCode(), RespCode.CODE_10200.getMsg(), data);
    }

    public RespBody(RespCode code) {
        this(code.getCode(), code.getMsg(), null);
    }

    private RespBody(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
