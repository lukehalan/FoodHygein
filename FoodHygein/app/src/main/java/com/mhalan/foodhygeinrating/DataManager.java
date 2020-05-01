/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {

    private static DataManager dataManager;
    private List<GeneralParameter> regionList;
    private List<GeneralParameter> businessTypeList;
    private List<String> favouriteIdList;
    private List<GeneralParameter> authorityList;


    private DataManager() {
        regionList = new ArrayList<>();
        businessTypeList = new ArrayList<>();
        favouriteIdList = new ArrayList<>();
        authorityList = new ArrayList<>();
        regionList.add(new Region(null, "All"));
        businessTypeList.add(new BusinessType(null, "All"));
    }

    public static synchronized DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public List<GeneralParameter> getBusinessTypeList() {
        return businessTypeList;
    }

    public List<String> getFavouriteIdList() {
        return favouriteIdList;
    }

    public void setFavouriteIdList(String[] favouriteIds) {
        this.favouriteIdList.addAll(Arrays.asList(favouriteIds));
    }

    public List<GeneralParameter> getRegionList() {
        return regionList;
    }

    public List<GeneralParameter> getAuthorityList() {
        return authorityList;
    }


    public void addBusinessType(BusinessType businessType) {
        if (businessType != null && !businessType.getId().equals("-1")) {
            this.businessTypeList.add(businessType);
        }
    }

    public void addRegion(Region region) {
        if (region != null) {
            this.regionList.add(region);
        }
    }

    public void removeFavouriteId(String fshid) {
        this.favouriteIdList.remove(fshid);
    }

    public void addFavouriteId(String fhrsid) {
        this.favouriteIdList.add(fhrsid);
    }

    public void addLocalAuthority(LocalAuthority localAuthority) {
        if (localAuthority != null) {
            this.authorityList.add(localAuthority);
        }
    }

    public ArrayList<GeneralParameter> getAuthoritiesForRegion(String regionName) {
        ArrayList<GeneralParameter> results = new ArrayList<>();
        for (GeneralParameter authority : this.authorityList) {
            if (regionName.equals(((LocalAuthority) authority).getRegionName())) {
                results.add(authority);
            }
        }
        return results;
    }

}
