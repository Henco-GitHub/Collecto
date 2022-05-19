package com.example.collecto;

import java.io.Serializable;
import java.util.Date;

public class Item {
    private String name;
    private String description;
    private Date date;
    private String pic;

    public Item(String name, String description, Date date, String pic) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.pic = pic;
    }

    public Item() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
