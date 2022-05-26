package com.example.collecto;

import java.util.Date;

public class ModelItem {
    private String id, name, description, pic, collection, uid, date;

    public ModelItem() {
    }

    public ModelItem(String id, String name, String description, String date, String pic, String collection, String uid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.pic = pic;
        this.collection = collection;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
