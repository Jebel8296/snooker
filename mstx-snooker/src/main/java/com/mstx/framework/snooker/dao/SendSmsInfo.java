package com.mstx.framework.user.dao;

import java.util.Date;

public class SendSmsInfo {
    private String mid;

    private String phonenum;

    private String verfyCode;

    private String smsMsg;

    private Date createTime;

    private String remark;

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid == null ? null : mid.trim();
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum == null ? null : phonenum.trim();
    }

    public String getVerfyCode() {
        return verfyCode;
    }

    public void setVerfyCode(String verfyCode) {
        this.verfyCode = verfyCode == null ? null : verfyCode.trim();
    }

    public String getSmsMsg() {
        return smsMsg;
    }

    public void setSmsMsg(String smsMsg) {
        this.smsMsg = smsMsg == null ? null : smsMsg.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}