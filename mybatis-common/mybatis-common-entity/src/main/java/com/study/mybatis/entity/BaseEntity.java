package com.study.mybatis.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BaseEntity<Key extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Key id;
    private LocalDateTime crTime;
    private LocalDateTime upTime;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public LocalDateTime getCrTime() {
        return crTime;
    }

    public void setCrTime(LocalDateTime crTime) {
        this.crTime = crTime;
    }

    public LocalDateTime getUpTime() {
        return upTime;
    }

    public void setUpTime(LocalDateTime upTime) {
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
