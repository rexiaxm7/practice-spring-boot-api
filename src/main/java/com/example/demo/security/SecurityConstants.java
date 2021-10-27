package com.example.demo.security;

public class SecurityConstants {
    public static final String SECRET = "testSecretMonthyluReportSystemtestSecretMonthyluReportSystemtestSecretMonthyluReportSystem";
    public static final long EXPIRATION_TIME = 28_800_000; // 8hours
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGNUP_URL = "/signup";
    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_ID = "email"; // defalut:username
    public static final String PASSWORD = "password"; // default:password
}