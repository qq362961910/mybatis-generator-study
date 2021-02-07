package com.study.mybatis.entity;

import java.io.Serializable;

public class BaseEntity<Key extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Key id;
    private Long crTime;
    private Long upTime;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public Long getCrTime() {
        return crTime;
    }

    public void setCrTime(Long crTime) {
        this.crTime = crTime;
    }

    public Long getUpTime() {
        return upTime;
    }

    public void setUpTime(Long upTime) {
        this.upTime = upTime;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
            "id=" + id +
            ", crTime=" + crTime +
            ", upTime=" + upTime +
            '}';
    }
}
