package com.xinyan.trust.entity;

import lombok.Data;

import java.util.Date;

/**
 * 基类
 */
@Data
public abstract class BaseBean {
    private Date updateTime;
    private String data;
}
