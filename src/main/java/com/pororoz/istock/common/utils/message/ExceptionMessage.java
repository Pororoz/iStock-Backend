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
  public static final String PART_DUPLICATED = "이미 존재하는 part 입니다.";
  public static final String PART_NOT_FOUND = "존재하지 않는 part입니다.";
  public static final String PRODUCT_NUMBER_DUPLICATED = "중복된 product number입니다.";
  public static final String PRODUCT_NOT_FOUND = "해당 제품을 찾을 수 없습니다.";
  public static final String DUPLICATE_BOM = "location_number, product_id, part_id의 조합이 중복됩니다.";
  public static final String BOM_NOT_FOUND = "존재하지 않는 BOM입니다.";
  public static final String INVALID_SUB_ASSY_BOM = "Sub assy BOM은 sub assy가 필요하고 part가 null이어야 합니다.";
  public static final String SUB_ASSY_CANNOT_HAVE_SUB_ASSY = "Sub assy는 sub assy를 BOM으로 가질 수 없습니다.";
  public static final String INVALID_PRODUCT_BOM = "Product BOM은 part가 필요하고 sub assy가 null이어야 합니다.";
  public static final String SUB_ASSY_BOM_EXIST = "해당 제품의 BOM에 sub assy가 존재합니다. Sub assy는 sub assy를 가질 수 없습니다.";
  public static final String REGISTERED_AS_SUB_ASSY = "다른 제품의 BOM에 sub assy로 등록되어 있습니다.";
  public static final String SUB_ASSY_NOT_FOUND_BY_PRODUCT_NAME = "Sub assy 목록에 BOM의 productNumber와 일치하는 product가 없습니다.";
  public static final String SELF_DEMOTE_ROLE = "본인의 권한을 강등시킬 수 없습니다.";
  public static final String SELF_DELETE_ACCOUNT = "본인의 계정을 삭제 할 수 없습니다.";
  public static final String PRODUCT_STOCK_MINUS = "제품의 재고가 부족합니다.";
  public static final String PART_STOCK_MINUS = "부품의 재고가 부족합니다.";
  public static final String PRODUCT_OR_BOM_NOT_FOUND = "제품을 찾을 수 없거나 연관된 BOM이 없습니다.";
  public static final String BOM_AND_SUB_ASSY_NOT_MATCHED =
      "Sub assy로 등록된 BOM과 BOM의 product number로 찾은 sub assy의 개수가 일치하지 않습니다.\n"
          + "BOM의 product number로 찾을 수 없는 제품(sub assy)이 있습니다.";
  public static final String BOM_SUB_ASSY_DUPLICATED =
      "제품의 BOM에 이미 같은 sub assy가 존재합니다. 중복된 sub assy는 등록할 수 없습니다.";
  public static final String SUB_ASSY_NOT_FOUND = "해당 sub assy를 찾을 수 없습니다.";
  public static final String PRODUCT_IO_NOT_FOUND = "해당 product io를 찾을 수 없습니다.";
  public static final String CHANGE_IO_STATUS = "잘못된 io 상태 수정입니다.";
  public static final String PART_IO_NOT_FOUND = "해당 part IO를 찾을 수 없습니다.";
  public static final String CHANGE_OUTBOUND_STATUS = "잘못된 outbound 수정 접근입니다.";

  private ExceptionMessage() {
  }
}
