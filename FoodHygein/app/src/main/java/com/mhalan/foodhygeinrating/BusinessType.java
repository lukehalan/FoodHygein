/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

public class BusinessType extends GeneralParameter {

    private String BusinessTypeId;
    private String BusinessTypeName;

    public BusinessType(String businessTypeId, String businessTypeName) {
        BusinessTypeId = businessTypeId;
        BusinessTypeName = businessTypeName;
    }

    @Override
    public String getId() {
        return BusinessTypeId;
    }

    @Override
    public String getShowName() {
        return BusinessTypeName;
    }
}

