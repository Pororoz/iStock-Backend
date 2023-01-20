package com.pororoz.istock.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResultDTO<D> {

    @NotBlank
    private final String status;

    @NotBlank
    private final String message;

    @NotNull
    private final D data;
}
