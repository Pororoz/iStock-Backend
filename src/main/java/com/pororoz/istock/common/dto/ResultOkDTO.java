package com.pororoz.istock.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResultOkDTO<D> {

    @Schema(description = "Result Code", example = "200")
    private String resultCode;

    @Schema(description = "Message", example = "Success")
    private String message;

    private D data;
}
