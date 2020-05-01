/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Establishment {
    @PrimaryKey
    @NonNull
    private String FHRSID;
    @ColumnInfo(name = "business_name")
    private String BusinessName;
    @ColumnInfo(name = "business_type")
    private String BusinessType;
    @ColumnInfo(name = "rating_value")
    private String RatingValue;
    @ColumnInfo(name = "rating_date")
    private String RatingDate;
    @Ignore
    private String AddressLine1;
    @Ignore
    private String AddressLine2;
    @Ignore
    private String AddressLine3;
    @Ignore
    private String AddressLine4;
    @Ignore
    private String PostCode;
    @Ignore
    private String LocalAuthorityCode;
    @Ignore
    private String LocalAuthorityName;
    @Ignore
    private String LocalAuthorityWebSite;
    @Ignore
    private String LocalAuthorityEmailAddress;
    @Ignore
    private GeoCode geocode;
    @Ignore
    private double Distance;
    @Ignore
    private Scores scores;
    @Ignore
    private String SchemeType;

    public Establishment() {

    }

    public String getFHRSID() {
        return FHRSID;
    }

    public String getBusinessName() {
        return BusinessName;
    }

    public String getBusinessType() {
        return BusinessType;
    }


    public String getRatingValue() {
        return RatingValue;
    }

    public String getPostCode() {
        return PostCode;
    }

    public String getRatingDate() {
        return RatingDate;
    }

    public String getLocalAuthorityCode() {
        return LocalAuthorityCode;
    }

    public String getLocalAuthorityName() {
        return LocalAuthorityName;
    }

    public String getLocalAuthorityWebSite() {
        return LocalAuthorityWebSite;
    }

    public String getLocalAuthorityEmailAddress() {
        return LocalAuthorityEmailAddress;
    }

    public GeoCode getGeocode() {
        return geocode;
    }

    public double getDistance() {
        return Distance;
    }

    public Scores getScores() {
        return scores;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public String getAddressLine3() {
        return AddressLine3;
    }

    public String getAddressLine4() {
        return AddressLine4;
    }


    public void setFHRSID(@NonNull String FHRSID) {
        this.FHRSID = FHRSID;
    }

    public void setBusinessName(String businessName) {
        BusinessName = businessName;
    }

    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }

    public void setRatingValue(String ratingValue) {
        RatingValue = ratingValue;
    }

    public void setRatingDate(String ratingDate) {
        RatingDate = ratingDate;
    }

    public String getSchemeType() {
        return SchemeType;
    }

    public void setAddressLine1(String addressLine1) {
        AddressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        AddressLine2 = addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        AddressLine3 = addressLine3;
    }

    public void setAddressLine4(String addressLine4) {
        AddressLine4 = addressLine4;
    }

    public void setPostCode(String postCode) {
        PostCode = postCode;
    }

    public void setLocalAuthorityCode(String localAuthorityCode) {
        LocalAuthorityCode = localAuthorityCode;
    }

    public void setLocalAuthorityName(String localAuthorityName) {
        LocalAuthorityName = localAuthorityName;
    }

    public void setLocalAuthorityWebSite(String localAuthorityWebSite) {
        LocalAuthorityWebSite = localAuthorityWebSite;
    }

    public void setLocalAuthorityEmailAddress(String localAuthorityEmailAddress) {
        LocalAuthorityEmailAddress = localAuthorityEmailAddress;
    }

    public void setGeocode(GeoCode geocode) {
        this.geocode = geocode;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public void setSchemeType(String schemeType) {
        SchemeType = schemeType;
    }

    public boolean hasGeocode() {
        return this.getGeocode().getLatitude() != 0 || this.getGeocode().getLongitude() != 0;
    }

    public String getAddressFirstLine() {
        if (this.getAddressLine1() != null && !this.getAddressLine1().isEmpty()
                || this.getAddressLine2() != null && !this.getAddressLine2().isEmpty() )
            return this.getAddressLine1() + ", " + this.getAddressLine2();
        else
            return null;
    }

    public String getAddressSecondLine() {
        if (this.getAddressLine3() != null && !this.getAddressLine3().isEmpty()
                || this.getAddressLine4() != null && !this.getAddressLine4().isEmpty() )
            return this.getAddressLine3() + ", " + this.getAddressLine4();
        else
            return null;
    }
}
