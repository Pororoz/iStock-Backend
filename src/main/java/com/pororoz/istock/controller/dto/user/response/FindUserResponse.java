package com.pororoz.istock.controller.dto.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FindUserResponse {

    @Schema(description = "유저 Key", example = "1")
    private int id;

    @Schema(description = "유저 아이디", example = "pythonstrup")
    private String username;

    @Schema(description = "생성 시간")
    private LocalDateTime created_at;

}
