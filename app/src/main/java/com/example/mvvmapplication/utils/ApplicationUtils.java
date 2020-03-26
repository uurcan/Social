package com.example.mvvmapplication.utils;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApplicationUtils {
    public static String getDate (String dateString){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") DateFormat format1 = new SimpleDateFormat("MMM d, yyyy");
        if (date != null) {
            return format1.format(date);
        } return  "";
    }
    public static String getTime(String dateString){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        Date date = null;
        try {
            date = format1.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("h:mm a");
        Date netDate = (date);
        if (netDate != null) {
            return sdf.format(netDate);
        } return  "";
    }
    public static long getRandomNumber(){
        return (long) ((Math.random()  * ((100000) + 1)) + 0);
    }
}
