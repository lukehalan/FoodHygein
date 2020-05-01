/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import java.io.Serializable;

public class PhotoModel implements Serializable {

    private String name;
    private String path;
    private boolean isSelected = false;

    public PhotoModel(String name, String path) {
        this.name = name;
        this.path = path;
       // this.isSelected = false;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
