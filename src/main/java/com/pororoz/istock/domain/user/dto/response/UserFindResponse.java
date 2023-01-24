package com.pororoz.istock.domain.user.dto.response;

import java.util.Objects;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserFindResponse extends UserResponse {

  private String createdAt;
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
    UserFindResponse that = (UserFindResponse) o;
    return Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt,
        that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, roleName, createdAt, updatedAt);
  }
}
