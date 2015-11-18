# HackerDetectionSystem

Hacker detection system - Requirements

 

A system provided by a company allows customers to sign in using their username and password. There is a requirement for an automated system to be developed to help identify attempts to hack the system and compromise accounts. Activity log files are recorded and the new system will need to process these logs to identify suspicious activity.  

 
Task is to write a Java implementation of the HackerDetector interface outlined below, which defines a single method 'parseLine'. The method will be called with one log line at a time and must return the IP address if any suspicious activity is identified or null if the activity appears to be normal.  

 

package com.example.detector; 

 

public interface HackerDetector { 

    public String parseLine(String line); 

} 



The parseline method will be called each time a new log line is produced. 

The log lines will be in the following format: 

 

            ip,date,action,username 

 

IP look like 80.238.9.179 

Date is in the epoch format like 1336129471 

Action is one of the following: SIGNIN_SUCCESS or SIGNIN_FAILURE 

Username is a String like Bruce.Banner 

 

A log line will therefore look like this: 

80.238.9.179,133612947,SIGNIN_SUCCESS,Bruce.Banner 

 

The first detection method will be to identify a single IP address that has attempted a failed login 5 or more times within a 5 minute period. On detection you should return the suspicious IP. 

 

Our signin page can generate around 100,000 failed signin requests per day, so memory consumption should be considered and managed. 
