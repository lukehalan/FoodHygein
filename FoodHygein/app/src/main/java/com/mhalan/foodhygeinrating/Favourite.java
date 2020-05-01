/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = Establishment.class, version = 2, exportSchema = false)
public abstract class Favourite  extends RoomDatabase {
    public abstract IEstablishmentDao iEstablishmentDao();
}
