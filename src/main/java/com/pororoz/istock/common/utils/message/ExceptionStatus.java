package com.pororoz.istock.common.utils.message;

public class ExceptionStatus {

  // Controlled Range
  public static final String BAD_REQUEST = "BAD_REQUEST";
  public static final String PAGE_NOT_FOUND = "PAGE_NOT_FOUND";
  public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  public static final String CATEGORY_NOT_FOUND = "CATEGORY_NOT_FOUND";
  public static final String PART_NAME_DUPLICATED = "PART_NAME_DUPLICATED";
  public static final String PART_NOT_FOUND = "PART_NOT_FOUND";
  public static final String PRODUCT_NUMBER_DUPLICATED = "PRODUCT_NUMBER_DUPLICATED";
  public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
  public static final String NOT_EXISTED_PART = "NOT_EXISTED_PART";
  public static final String DUPLICATE_BOM = "DUPLICATE_BOM_COMBINATION";

  // Uncontrolled Range
  public static final String RUNTIME_ERROR = "RUNTIME_ERROR";
  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
  public static final String FORBIDDEN = "FORBIDDEN";

  private ExceptionStatus() {
  }
}