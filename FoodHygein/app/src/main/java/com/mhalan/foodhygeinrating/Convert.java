/*
 * Copyright (c) 2019. Mohammad Halan - Portfolio Assignment
 */

package com.mhalan.foodhygeinrating;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Convert {

    public static String dateToString(String dateString, String inDate, String outDate) throws ParseException {
        String date = "";
        Date in = new SimpleDateFormat(inDate).parse(dateString);
        date = new SimpleDateFormat(outDate).format(in);
        return date;

    }
}
