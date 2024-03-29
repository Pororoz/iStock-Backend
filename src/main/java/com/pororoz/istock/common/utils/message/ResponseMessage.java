package com.pororoz.istock.common.utils.message;

public class ResponseMessage {

  public static final String LOGIN = "로그인";
  public static final String LOGOUT = "로그아웃";
  public static final String SAVE_USER = "계정 생성";
  public static final String DELETE_USER = "계정 삭제";
  public static final String FIND_USER = "계정 조회";
  public static final String UPDATE_USER = "계정 수정";
  public static final String SAVE_CATEGORY = "카테고리 생성";
  public static final String DELETE_CATEGORY = "카테고리 삭제";
  public static final String UPDATE_CATEGORY = "카테고리 수정";
  public static final String FIND_CATEGORY = "카테고리 조회";
  public static final String SAVE_PART = "부품 생성";
  public static final String DELETE_PART = "부품 삭제";
  public static final String UPDATE_PART = "부품 수정";
  public static final String FIND_PART = "부품 조회";
  public static final String SAVE_PRODUCT = "제품 생성";
  public static final String UPDATE_PRODUCT = "제품 수정";
  public static final String DELETE_PRODUCT = "제품 삭제";
  public static final String FIND_PRODUCT = "제품 조회";
  public static final String FIND_BOM = "BOM 행 조회";
  public static final String SAVE_BOM = "BOM 행 추가";
  public static final String DELETE_BOM = "BOM 행 제거";
  public static final String UPDATE_BOM = "BOM 행 수정";
  public static final String WAIT_PRODUCTION = "제품 생산 대기";
  public static final String CONFIRM_PRODUCTION = "제품 생산 완료";
  public static final String CANCEL_PRODUCTION = "제품 생산 취소";
  public static final String PURCHASE_PRODUCT = "제품 자재 일괄 구매 대기";
  public static final String PURCHASE_PART = "제품 자재 개별 구매 대기";
  public static final String CONFIRM_PURCHASE_PART = "제품 자재 구매 확정";
  public static final String CANCEL_PURCHASE_PART = "제품 자재 구매 취소";
  public static final String OUTBOUND_WAIT = "제품 출고 대기";
  public static final String CONFIRM_SUB_ASSY_OUTSOURCING = "Sub Assy 외주 생산 확정";
  public static final String CANCEL_SUB_ASSY_OUTSOURCING = "Sub Assy 외주 생산 취소";
  public static final String CONFIRM_SUB_ASSY_PURCHASE = "Sub Assy 구매 확정";
  public static final String CANCEL_SUB_ASSY_PURCHASE = "Sub Assy 구매 취소";
  public static final String OUTBOUND_CONFIRM = "제품 출고 확정";
  public static final String OUTBOUND_CANCEL = "제품 출고 취소";
  public static final String FIND_PRODUCT_IO = "제품 IO 조회";
    public static final String FIND_PART_IO = "부품 IO 조회";

  public static final String UPLOAD_CSV = "CSV 업로드";
  private ResponseMessage() {
  }
}
