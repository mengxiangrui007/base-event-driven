package com.risen.base.event.driven.example.kafka;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Long id;
    private String name;
    private Date cdate;

    public User() {
    }

    public User(Long id, String name, Date cdate) {
        this.id = id;
        this.name = name;
        this.cdate = cdate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }
}