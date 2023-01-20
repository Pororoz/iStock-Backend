package com.pororoz.istock.common.utils.message;

public class ExceptionStatus {

    // Controlled Range
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String PAGE_NOT_FOUND = "PAGE_NOT_FOUND";

    public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";

    // Uncontrolled Range
    public static final  String RUNTIME_ERROR = "RUNTIME_ERROR";
    public static final  String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";


    private ExceptionStatus() {}
}