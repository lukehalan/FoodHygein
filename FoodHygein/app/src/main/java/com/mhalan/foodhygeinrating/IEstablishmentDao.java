/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface IEstablishmentDao {

    @Insert
    void insert(Establishment establishment);

    @Delete
    void delete(Establishment establishment);

    @Query("SELECT * FROM establishment")
    Establishment[] selectAll();

    @Query("SELECT FHRSID FROM establishment")
    String[] selectIds();

}
