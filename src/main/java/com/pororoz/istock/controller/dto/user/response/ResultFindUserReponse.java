package com.pororoz.istock.controller.dto.user.response;

import com.pororoz.istock.common.dto.ResultOkDTO;

public class ResultFindUserReponse extends ResultOkDTO<FindUserResponse> {
    public ResultFindUserReponse(String resultCode, String message, FindUserResponse data) {
        super(resultCode, message, data);
    }
}
