package com.pororoz.istock.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResultBadRequestDTO {
    @Schema(description = "Result Code", example = "400")
    private String resultCode;

    @Schema(description = "Message", example = "Bad Request")
    private String message;

    @Schema(description = "Empty Object", example = "{}")
    private Object data;
}
