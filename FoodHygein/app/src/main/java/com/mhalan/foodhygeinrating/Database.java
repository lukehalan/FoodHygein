/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import android.arch.persistence.room.Room;
import android.content.Context;

public class Database {
    private static Database database;
    private Favourite db;


    public static synchronized Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }
    public Favourite getDb(Context context) {
        if (this.db == null) {
            db = Room.databaseBuilder(context, Favourite.class, "favourites")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return this.db;
    }
}
