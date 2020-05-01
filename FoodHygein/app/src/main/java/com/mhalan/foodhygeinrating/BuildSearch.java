/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BuildSearch implements Serializable {
    private String address;
    private String name;
    private String latitude;
    private String longitude;
    private String ratingKey;
    private String businessTypeId;
    private String maxDistanceLimit;
    private String ratingOperatorKey;
    private String schemeTypeKey;
    private String localAuthorityId;
    private String countryId;
    private Integer pageNumber;
    private Integer pageSize;
    private String sortOptionKey = "rating";


    private static final String[] sortingOptions = {"rating", "desc_rating", "Relevance", "distance"};
    private static final String[] fhisRatingKeys = {"Pass", "ImprovementRequired", "AwaitingPublication", "AwaitingInspection", "Exempt"};


    public BuildSearch() {
        this.reset();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getMaxDistanceLimit() {
        return maxDistanceLimit;
    }

    public void setMaxDistanceLimit(String maxDistanceLimit) {
        this.maxDistanceLimit = maxDistanceLimit;
    }

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getRatingKey() {
        return ratingKey;
    }

    public void setRatingKey(String ratingKey) {
        this.ratingKey = ratingKey;
    }

    public String getLocalAuthorityId() {
        return localAuthorityId;
    }

    public void setLocalAuthorityId(String localAuthorityId) {
        this.localAuthorityId = localAuthorityId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getSchemeTypeKey() {
        return schemeTypeKey;
    }

    public void setSchemeTypeKey(String schemeTypeKey) {
        this.schemeTypeKey = schemeTypeKey;
    }

    public String getSortOptionKey() {
        return sortOptionKey;
    }


    public String getRatingOperatorKey() {
        return ratingOperatorKey;
    }

    public void setRatingOperatorKey(String ratingOperatorKey) {
        this.ratingOperatorKey = ratingOperatorKey;
    }

    public void setFhisRatingKey(int position) {
        this.ratingKey = fhisRatingKeys[position];
    }


    public void setSortOptionKey(int position) {
        if (position == -1) {
            this.sortOptionKey = null;
            return;
        }
        sortOptionKey = sortingOptions[position];
    }

    public boolean isFHIS() {
        return this.schemeTypeKey != null;
    }

    public void setRatingOperator(String operator) {
        this.ratingOperatorKey = operator.replace(" ", "");
    }

    public boolean isLocationUsed() {
        return latitude != null && longitude != null;
    }

    public void next() {
        this.pageNumber = pageNumber + 1;
    }

    public void reset() {
        this.pageNumber = 1;
        this.pageSize = 10;
    }

    private Map<String, String> toHashMap() {
        HashMap hashMap = new HashMap<String, String>();
        Gson gson = new Gson();
        String json = gson.toJson(this);
        hashMap = gson.fromJson(json, hashMap.getClass());
        Log.e("hash", hashMap.toString());
        return hashMap;
    }

    public String buildSearchString() {
        StringBuilder builder = new StringBuilder();
        Map<String, String> map = this.toHashMap();

        if (map.keySet().size() == 0) {
            return "";
        } else {
            builder.append("?");
        }

        for (String key : map.keySet()) {
            builder.append(key).append("=").append(String.valueOf(map.get(key)).replace(" ", "%20")
                    .replace(".0", "")).append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
