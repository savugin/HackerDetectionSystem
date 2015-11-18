package com.example.converter;

import java.util.Date;

public class DateConverter {

    /**
     * 
     * @param epochFormatDate
     *            (pass epoch format date)
     * @return
     */
    public static Date convertEpochDateFormatToDate(String epochFormatDate) {
        Date convertedDate = null;
        if (epochFormatDate != null && !epochFormatDate.trim().isEmpty()){
            if(epochFormatDate.trim().matches("[0-9]+")){
                convertedDate = new Date(Long.parseLong(epochFormatDate.trim()) * 1000L);
            }
        }
        return convertedDate;
    }
}
