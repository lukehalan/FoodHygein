/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

public class LocalAuthority extends GeneralParameter {

    private String LocalAuthorityId;
    private String RegionName;
    private String Name;


    public LocalAuthority(String id, String name) {
        LocalAuthorityId = id;
        Name = name;
    }

    public String getRegionName() {
        return RegionName;
    }

    @Override
    public String getId() {
        return LocalAuthorityId;
    }

    @Override
    public String getShowName() {
        return Name;
    }

}
