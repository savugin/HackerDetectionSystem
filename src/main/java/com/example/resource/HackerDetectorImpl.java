package com.example.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.converter.DateConverter;
import com.example.detector.HackerDetector;
import com.example.enums.Actions;

/**
 * This class detect the ipAddress of user who try to login with invalid details
 * more than 5 times within 5 min.
 * 
 * @author SAvugin
 * 
 */
public class HackerDetectorImpl implements HackerDetector {

    private static final Logger logger = Logger.getLogger(HackerDetectorImpl.class.getName());
    private ConcurrentHashMap<String, ArrayList<Date>> failedLoginMap = new ConcurrentHashMap<String, ArrayList<Date>>();
    private int faliedLoginCount = 0;

    /**
     * @param line
     *            - Pass a string line which in following format
     *            "ipAddress, date, action, username" - Example:
     *            "80.238.9.179,1336129750,SIGNIN_FAILURE,Bruce.Banner")
     * 
     */
    public String parseLine(String line) {
        String[] splittedWords = line != null ? line.split(",") : null;
        Actions action = null;
        if (splittedWords != null && splittedWords.length == 4) {
            action = extractAction(splittedWords);
        } else {
            logger.log(Level.WARNING, "Logged line not in correct format : ");
        }

        if (action != null && action.equals(Actions.SIGNIN_FAILURE)) {
            ArrayList<Date> failedLoginDates = getUpdatedFailedLoginDates(splittedWords);
            if (failedLoginDates != null && failedLoginDates.size() >= 5) {
                return splittedWords[0];
            }
        }
        return null;
    }

    public Actions extractAction(String[] splittedWords) {
        Actions action = null;
        try {
            action = Actions.valueOf(splittedWords[2].trim());
        } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING,
                    "action should be either SIGNIN_SUCCESS or SIGNIN_FAILURE, IP Address : " + splittedWords[0], ex);
        }
        return action;
    }

    public ArrayList<Date> getUpdatedFailedLoginDates(String[] splittedWords) {
        ArrayList<Date> failedLoginDates = null;
        String ipAddress = splittedWords[0].trim();
        Date latestFailedLoginDate = DateConverter.convertEpochDateFormatToDate(splittedWords[1].trim());
        failedLoginDates = getFailedLoginMap().get(ipAddress);
        if (latestFailedLoginDate == null) {
            logger.log(Level.WARNING,
                    "Failed Signin date not in correct format or null for IP Address : " + splittedWords[0]);
            return failedLoginDates;
        }
        
        if (failedLoginDates == null) {
            failedLoginDates = new ArrayList<Date>();
            failedLoginDates.add(latestFailedLoginDate);
            getFailedLoginMap().putIfAbsent(ipAddress, failedLoginDates);
            faliedLoginCount++;
        } else {
            deleteFiveMinuteOlderDates(latestFailedLoginDate, failedLoginDates);
            failedLoginDates.add(latestFailedLoginDate);
            faliedLoginCount++;
        }
        
        if (faliedLoginCount == 1000) {
            deleteFiveMinuteOlderEvery1000Failure(latestFailedLoginDate);
        }
        return failedLoginDates;
    }

    public void deleteFiveMinuteOlderEvery1000Failure(Date latestFailedLoginDate) {
        for (Entry<String, ArrayList<Date>> entry : getFailedLoginMap().entrySet()) {
            ArrayList<Date> failedLoginDates = entry.getValue();
            deleteFiveMinuteOlderDates(latestFailedLoginDate, failedLoginDates);
            if (failedLoginDates.isEmpty()) {
                getFailedLoginMap().remove(entry.getKey());
            }
        }
        faliedLoginCount = 0;
    }

    public void deleteFiveMinuteOlderDates(Date latestFailedLoginDate, ArrayList<Date> failedLoginDates) {
        ArrayList<Date> dateToRemove = new ArrayList<Date>();
        for (Date date : failedLoginDates) {
            float timeDifferenceInMinute = calculateTimeDifferenceInMin(latestFailedLoginDate, date);
            if (timeDifferenceInMinute > 5) {
                dateToRemove.add(date);
                continue;
            } else if (timeDifferenceInMinute <= 5) {
                break;
            }
        }
        if (dateToRemove.size() > 0) {
            failedLoginDates.removeAll(dateToRemove);
        }
    }

    public float calculateTimeDifferenceInMin(Date latestFailedLoginDate, Date date) {
        float timeDifferenceInMinute = 0;
        if (date != null && latestFailedLoginDate != null) {
            timeDifferenceInMinute = (latestFailedLoginDate.getTime() - date.getTime()) / (1000f * 60f);
        }
        return timeDifferenceInMinute;
    }

    public ConcurrentHashMap<String, ArrayList<Date>> getFailedLoginMap() {
        return failedLoginMap;
    }

    public int getFaliedLoginCount() {
        return faliedLoginCount;
    }
}
