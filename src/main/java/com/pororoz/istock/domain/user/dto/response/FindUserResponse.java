package com.pororoz.istock.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindUserResponse extends UserResponse {

  @Schema(description = "생성 시간", example = "2023-01-01 00:00:00")
  private String createdAt;
  @Schema(description = "수정 시간", example = "2023-01-01 00:00:00")
  private String updatedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FindUserResponse that = (FindUserResponse) o;
    return Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt,
        that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, roleName, createdAt, updatedAt);
  }
}
