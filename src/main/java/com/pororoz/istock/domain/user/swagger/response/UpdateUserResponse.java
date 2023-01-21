package com.pororoz.istock.domain.user.swagger.response;

import com.pororoz.istock.common.utils.message.ResponseMessage;
import com.pororoz.istock.common.utils.message.ResponseStatus;
import com.pororoz.istock.domain.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserResponse {
    @Schema(description = "Result Code", example = ResponseStatus.OK)
    private String status;

    @Schema(description = "Message", example = ResponseMessage.UPDATE_USER)
    private String message;

    private UserResponse userResponse;
}
