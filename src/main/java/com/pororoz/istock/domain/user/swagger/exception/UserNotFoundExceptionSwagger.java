package com.pororoz.istock.domain.user.swagger.exception;

import com.pororoz.istock.common.utils.message.ExceptionMessage;
import com.pororoz.istock.common.utils.message.ExceptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserNotFoundExceptionSwagger {
    @Schema(description = "에러 명칭", example = ExceptionStatus.USER_NOT_FOUND)
    private String status;

    @Schema(description = "상세 메시지", example = ExceptionMessage.USER_NOT_FOUND)
    private String message;
}
