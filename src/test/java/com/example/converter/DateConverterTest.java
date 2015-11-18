package com.example.converter;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateConverterTest {

    @Test
    public void convertEpochDateFormatToDate_Pass_Null() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate(null);
        Assert.assertNull(convertedDateFormat);
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_EmptyString() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("");
        Assert.assertNull(convertedDateFormat);
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_WhiteSpace() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("  ");
        Assert.assertNull(convertedDateFormat);
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_InvalidDate_Alphabets() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("abcd");
        Assert.assertNull(convertedDateFormat);
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_InvalidDate_Alphabets_And_Numbers() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("abcd1234");
        Assert.assertNull(convertedDateFormat);
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_ValidDate() throws ParseException {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("1336129471");
        Assert.assertEquals("Fri May 04 12:04:31 BST 2012", convertedDateFormat.toString());
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_With_ValidDate_WhiteSpace() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("1336129471  ");
        Assert.assertEquals("Fri May 04 12:04:31 BST 2012", convertedDateFormat.toString());
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_With_WhiteSpace_ValidDate() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("  1336129471");
        Assert.assertEquals("Fri May 04 12:04:31 BST 2012", convertedDateFormat.toString());
    }

    @Test
    public void convertEpochDateFormatToDate_Pass_With_WhiteSpace_ValidDate_WhiteSpace() {
        Date convertedDateFormat = DateConverter.convertEpochDateFormatToDate("  1336129471");
        Assert.assertEquals("Fri May 04 12:04:31 BST 2012", convertedDateFormat.toString());
    }
}
