package com.pororoz.istock.common.utils.message;

public class ExceptionMessage {
  public static final String UNAUTHORIZED = "로그인 정보가 일치하지 않습니다.";
  public static final String PAGE_NOT_FOUND = "해당 페이지는 존재하지 않습니다.";
  public static final String ROLE_NOT_FOUND = "해당 Role이 존재하지 않습니다.";
  public static final String USER_NOT_FOUND = "해당 아이디에 맞는 유저를 찾을 수 없습니다.";
  public static final String CATEGORY_NOT_FOUND = "해당 카테고리를 찾을 수 없습니다.";
  public static final String INVALID_PASSWORD = "형식에 맞지 않는 비밀번호입니다.";
  public static final String INVALID_PATH = "형식에 맞지 않는 path 입니다.";
  public static final String INVALID_ROLENAME = "role 이름은 빈 값이 될 수 없습니다.";
  public static final String INVALID_ID = "형식에 맞지 않는 ID 입니다.";
  public static final String TYPE_MISMATCH = "맞지 않는 타입입니다.";
  public static final String INTERNAL_SERVER_ERROR = "내부 서버 오류입니다.";
  public static final String INVALID_PAGE_REQUEST = "page는 0 이상, size는 1 이상을 입력해주세요.";
  public static final String INVALID_CATEGORY_NAME = "카테고리는 2자 이상, 15자 이하로 입력해주세요.";
  public static final String PART_NAME_DUPLICATED = "이미 존재하는 part 입니다.";
  public static final String FORBIDDEN = "사용이 거절되었습니다.";
  public static final String PRODUCT_NUMBER_DUPLICATED = "중복된 product number입니다.";
  public static final String PRODUCT_NOT_FOUND = "해당 제품을 찾을 수 없습니다.";
  public static final String NOT_EXISTED_PART = "존재하지 않는 part입니다.";
  public static final String DUPLICATE_BOM = "location_number, product_id, part_id의 조합이 중복됩니다.";
  public static final String BOM_NOT_FOUND = "존재하지 않는 BOM ID를 조회했습니다.";

  private ExceptionMessage() {
  }
}
