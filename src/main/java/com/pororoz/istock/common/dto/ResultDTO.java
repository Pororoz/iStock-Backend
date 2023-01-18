package com.pororoz.istock.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResultDTO<D> {

    @Schema(description = "Result Code", example = "OK")
    @NotBlank
    private final String status;

    @Schema(description = "Message", example = "Success")
    @NotBlank
    private final String message;

    @NotNull
    private final D data;
}
