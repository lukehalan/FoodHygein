/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

public abstract class GeneralParameter {

    public abstract String getId();

    public abstract String getShowName();

    @Override
    public String toString() {
        return this.getShowName();
    }
}

