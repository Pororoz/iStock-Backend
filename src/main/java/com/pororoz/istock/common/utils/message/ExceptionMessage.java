package com.pororoz.istock.common.utils.message;

public class ExceptionMessage {

  public static final String BAD_REQUEST = "잘못된 요청을 보냈습니다.";
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
  public static final String PARAMETER_REQUIRED = "파리미터가 필요합니다.";
  public static final String FORBIDDEN = "사용이 거절되었습니다.";
  public static final String INTERNAL_SERVER_ERROR = "내부 서버 오류입니다.";
  public static final String INVALID_PAGE_REQUEST = "page는 0 이상, size는 1 이상을 입력해주세요.";
  public static final String INVALID_CATEGORY_NAME = "카테고리는 2자 이상, 15자 이하로 입력해주세요.";
  public static final String PART_NAME_DUPLICATED = "이미 존재하는 part 입니다.";
  public static final String PART_NOT_FOUND = "존재하지 않는 part입니다.";
  public static final String PRODUCT_NUMBER_DUPLICATED = "중복된 product number입니다.";
  public static final String PRODUCT_NOT_FOUND = "해당 제품을 찾을 수 없습니다.";
  public static final String DUPLICATE_BOM = "location_number, product_id, part_id의 조합이 중복됩니다.";
  public static final String BOM_NOT_FOUND = "존재하지 않는 BOM입니다.";
  public static final String INVALID_SUB_ASSY_BOM = "sub assy는 완제품의 productNumber가 필요하고 partId가 null이어야 합니다.";
  public static final String INVALID_PRODUCT_BOM = "product BOM은 partId가 필요하고 productNumber가 null이어야 합니다.";
  public static final String SUB_ASSY_BOM_EXIST = "해당 제품의 BOM에 sub assy가 존재합니다. Sub assy는 sub assy를 가질 수 없습니다.";
  public static final String REGISTERED_AS_SUB_ASSY = "다른 제품의 BOM에 sub assy로 등록되어 있습니다.";

  private ExceptionMessage() {
  }
}
