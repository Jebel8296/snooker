package com.mstx.framework.user.dao;

import java.util.Date;

public class TestUser {
    private Short id;

    private String name;

    private Date createTiime;

    private Date updateTime;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getCreateTiime() {
        return createTiime;
    }

    public void setCreateTiime(Date createTiime) {
        this.createTiime = createTiime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}