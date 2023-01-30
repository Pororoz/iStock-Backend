package com.pororoz.istock.common.utils.message;

public class ExceptionMessage {

  public static final String PAGE_NOT_FOUND = "해당 페이지는 존재하지 않습니다.";
  public static final String ROLE_NOT_FOUND = "해당 Role이 존재하지 않습니다.";
  public static final String USER_NOT_FOUND = "해당 아이디에 맞는 유저를 찾을 수 없습니다.";
  public static final String CATEGORY_NOT_FOUND = "해당 카테고리를 찾을 수 없습니다.";
  public static final String INVALID_PASSWORD = "형식에 맞지 않는 비밀번호입니다.";
  public static final String INVALID_PATH = "형식에 맞지 않는 path 입니다.";
  public static final String INVALID_ROLENAME = "role 이름은 빈 값이 될 수 없습니다.";
  public static final String INVALID_ID = "형식에 맞지 않는 ID 입니다.";
  public static final String TYPE_MISMATCH = "맞지 않는 타입입니다.";
  public static final String INTERTNAL_SERVER_ERROR = "내부 서버 오류입니다.";
  public static final String INVALID_PAGE_REQUEST = "page는 0 이상, size는 1 이상을 입력해주세요.";

  private ExceptionMessage() {
  }
}
