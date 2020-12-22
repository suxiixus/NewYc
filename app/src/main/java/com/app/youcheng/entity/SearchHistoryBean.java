package com.app.youcheng.entity;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class SearchHistoryBean extends DataSupport implements Serializable {

    public SearchHistoryBean(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
