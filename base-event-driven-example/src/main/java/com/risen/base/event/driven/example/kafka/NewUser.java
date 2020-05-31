package com.risen.base.event.driven.example.kafka;

import java.io.Serializable;
import java.util.Date;

public class NewUser implements Serializable {
    private Long id;
    private String name;
    private Date cdate;
    private Date mdate;

    public NewUser(Long id, String name, Date cdate, Date mdate) {
        this.id = id;
        this.name = name;
        this.cdate = cdate;
        this.mdate = mdate;
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

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }
}