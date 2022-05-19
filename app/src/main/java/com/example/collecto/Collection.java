package com.example.collecto;

import java.io.Serializable;

public class Collection extends Items {
    private String name = "";
    private String description = "";

    public Collection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Collection() {
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
}
