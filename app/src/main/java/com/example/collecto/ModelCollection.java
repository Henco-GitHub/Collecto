package com.example.collecto;

public class ModelCollection {
    String id, name, description, uid;

    public ModelCollection() {
    }

    public ModelCollection(String id, String name, String description, String uid) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
