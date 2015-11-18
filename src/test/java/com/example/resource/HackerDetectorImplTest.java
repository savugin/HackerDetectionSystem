package com.example.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import com.example.converter.DateConverter;
import com.example.enums.Actions;

public class HackerDetectorImplTest {

    private HackerDetectorImpl hackerDetectorImpl;
    private ConcurrentHashMap<String, ArrayList<Date>> failedLoginMap;
    private static final String IPADDRESS = "80.238.9";

    @Before
    public void setUp() {
        hackerDetectorImpl = mock(HackerDetectorImpl.class, CALLS_REAL_METHODS);
        failedLoginMap = new ConcurrentHashMap<String, ArrayList<Date>>();
    }

    @Test
    public void parseLine_Pass_Null() {
        String hackDetectedIpAddress = hackerDetectorImpl.parseLine(null);
        Assert.assertNull(hackDetectedIpAddress);
    }

    @Test
    public void parseLine_Pass_Valid_Format_String() {
        String hackDetectedIpAddress = hackerDetectorImpl
                .parseLine("80.238.9.179,1336129471,SIGNIN_SUCCESS,Bruce.Banner");
        Assert.assertNull(hackDetectedIpAddress);
    }

    @Test
    public void parseLine_FailedLoginDates_Null() {
        when(hackerDetectorImpl.getFailedLoginMap()).thenReturn(failedLoginMap);
        String hackDetectedIpAddress = hackerDetectorImpl
                .parseLine("80.238.9.179,abcd,SIGNIN_FAILURE,Bruce.Banner");
        Assert.assertNull(hackDetectedIpAddress);
    }

    @Test
    public void parseLine_Return_SuspeciousIpAddress() {
        when(hackerDetectorImpl.getFailedLoginMap()).thenReturn(failedLoginMap);
        hackerDetectorImpl.parseLine("80.238.9,1336129750,SIGNIN_FAILURE,Bruce.Banner");
        hackerDetectorImpl.parseLine("80.238.9,1336129850,SIGNIN_FAILURE,Bruce.Banner");
        hackerDetectorImpl.parseLine("80.238.9,1336129900,SIGNIN_FAILURE,Bruce.Banner");
        hackerDetectorImpl.parseLine("80.238.9,1336129950,SIGNIN_FAILURE,Bruce.Banner");
        String suspeciousIpAddress = hackerDetectorImpl
                .parseLine("80.238.9,1336129955,SIGNIN_FAILURE,Bruce.Banner");
        Assert.assertEquals(suspeciousIpAddress, IPADDRESS);
    }

    @Test
    public void getUpdatedFailedLoginDates_valid_Action_SIGIN_FAILURE() {
        String[] splittedWords = { IPADDRESS, "1336129471", "SIGNIN_FAILURE", "Bruce.Banner" };
        when(hackerDetectorImpl.getFailedLoginMap()).thenReturn(failedLoginMap);
        ArrayList<Date> failedLoginDates = hackerDetectorImpl.getUpdatedFailedLoginDates(splittedWords);
        Assert.assertNotNull(failedLoginDates);
        Assert.assertEquals(failedLoginDates.size(), 1);
    }

    @Test
    public void getUpdatedFailedLoginDates_valid_failedLoginDates_Return_Value() {
        when(hackerDetectorImpl.getFailedLoginMap()).thenReturn(failedLoginMap);
        String[] splittedWords1 = { IPADDRESS, "1336129471", "SIGNIN_FAILURE", "Bruce.Banner" };
        String[] splittedWords2 = { IPADDRESS, "1336129492", "SIGNIN_FAILURE", "Bruce.Banner" };
        ArrayList<Date> failedLoginDates = hackerDetectorImpl
                .getUpdatedFailedLoginDates(splittedWords1);
        failedLoginDates = hackerDetectorImpl.getUpdatedFailedLoginDates(splittedWords2);
        Assert.assertNotNull(failedLoginDates);
        Assert.assertEquals(failedLoginDates.size(), 2);
    }

    @Test
    public void getUpdatedFailedLoginDates_valid_LatestFailedLoginDates_null() {
        when(hackerDetectorImpl.getFailedLoginMap()).thenReturn(failedLoginMap);
        String[] splittedWords1 = { IPADDRESS, "1336129471", "SIGNIN_FAILURE", "Bruce.Banner" };
        String[] splittedWords2 = { IPADDRESS, "", "SIGNIN_FAILURE", "Bruce.Banner" };
        ArrayList<Date> failedLoginDates = hackerDetectorImpl
                .getUpdatedFailedLoginDates(splittedWords1);
        failedLoginDates = hackerDetectorImpl.getUpdatedFailedLoginDates(splittedWords2);
        Assert.assertNotNull(failedLoginDates);
        Assert.assertEquals(failedLoginDates.size(), 1);
    }

    @Test
    public void extractAction_Invalid_Action() {
        String[] splittedWords = { IPADDRESS, "1336129471", "SIGNIN_", "Bruce.Banner" };
        Actions action = hackerDetectorImpl.extractAction(splittedWords);
        Assert.assertNull(action);
    }

    @Test
    public void extractAction_valid_InvalidSplittedWords() {
        String[] splittedWords = { "1336129471", "SIGNIN_FAILURE", "Bruce.Banner" };
        Actions action = hackerDetectorImpl.extractAction(splittedWords);
        Assert.assertNull(action);
    }

    @Test
    public void deleteFiveMinuteOlderEvery1000Failure_EmptyFailedLoginMap() {
        when(hackerDetectorImpl.getFailedLoginMap()).thenReturn(failedLoginMap);
        hackerDetectorImpl.deleteFiveMinuteOlderEvery1000Failure(DateConverter
                .convertEpochDateFormatToDate("1336128101"));
        Assert.assertTrue(hackerDetectorImpl.getFailedLoginMap().isEmpty());
        Assert.assertEquals(hackerDetectorImpl.getFaliedLoginCount(), 0);
    }

    @Test
    public void deleteFiveMinuteOlderDates_EmptyFailedLoginDates() {
        Date latestFailedLoginDate = null;
        ArrayList<Date> failedLoginDates = new ArrayList<Date>();
        hackerDetectorImpl.deleteFiveMinuteOlderDates(latestFailedLoginDate, failedLoginDates);
        Assert.assertTrue(failedLoginDates.isEmpty());
    }

    @Test
    public void deleteFiveMinuteOlderDates_FailedLoginDates_With_Null() {
        Date latestFailedLoginDate = null;
        ArrayList<Date> failedLoginDates = new ArrayList<Date>();
        failedLoginDates.add(null);
        hackerDetectorImpl.deleteFiveMinuteOlderDates(latestFailedLoginDate, failedLoginDates);
        Assert.assertNull(failedLoginDates.get(0));
    }

    @Test
    public void deleteFiveMinuteOlderDates_FailedLoginDates_With_valid_FiveMinuteOlderDate() {
        Date latestFailedLoginDate = DateConverter.convertEpochDateFormatToDate("1336129471");
        ArrayList<Date> failedLoginDates = new ArrayList<Date>();
        failedLoginDates.add(DateConverter.convertEpochDateFormatToDate("1336128101"));
        hackerDetectorImpl.deleteFiveMinuteOlderDates(latestFailedLoginDate, failedLoginDates);
        Assert.assertTrue(failedLoginDates.isEmpty());
    }

    @Test
    public void deleteFiveMinuteOlderDates_FailedLoginDates_With_valid_WithinFiveMinute() {
        Date latestFailedLoginDate = DateConverter.convertEpochDateFormatToDate("1336128200");
        ArrayList<Date> failedLoginDates = new ArrayList<Date>();
        failedLoginDates.add(DateConverter.convertEpochDateFormatToDate("1336128101"));
        hackerDetectorImpl.deleteFiveMinuteOlderDates(latestFailedLoginDate, failedLoginDates);
        Assert.assertEquals(failedLoginDates.size(), 1);
    }
}
