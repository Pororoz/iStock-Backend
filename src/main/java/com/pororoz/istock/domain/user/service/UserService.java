package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.FindUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceResponse updateUser(UpdateUserServiceRequest updateUserServiceRequest) {
    Role role = roleRepository.findByName(updateUserServiceRequest.getRoleName())
        .orElseThrow(RoleNotFoundException::new);
    User targetUser = userRepository.findById(updateUserServiceRequest.getId())
        .orElseThrow(UserNotFoundException::new);
    targetUser.setRole(role);
    targetUser.setPassword(updateUserServiceRequest.getPassword());

    return UserServiceResponse.of(targetUser);
  }

  public UserServiceResponse deleteUser(DeleteUserServiceRequest deleteUserServiceRequest) {
    User user = userRepository.findById(deleteUserServiceRequest.getId())
        .orElseThrow(UserNotFoundException::new);
    userRepository.deleteById(deleteUserServiceRequest.getId());
    return UserServiceResponse.of(user);
  }

  public UserServiceResponse saveUser(SaveUserServiceRequest saveUserServiceRequest) {
    String encodedPassword = passwordEncoder.encode(saveUserServiceRequest.getPassword());
    Role role = roleRepository.findByName(saveUserServiceRequest.getRoleName())
        .orElseThrow(RoleNotFoundException::new);
    User user = saveUserServiceRequest.toUser(encodedPassword, role);
    User result = userRepository.save(user);
    return UserServiceResponse.of(result);
  }

  @Transactional(readOnly = true)
  public Page<UserServiceResponse> findUsers(FindUserServiceRequest request) {
    if (request.getPage() == null && request.getSize() == null) {
      List<User> users = userRepository.findAll();
      return new PageImpl<>(users).map(UserServiceResponse::of);
    }
    return userRepository.findAll(request.toPageRequest()).map(UserServiceResponse::of);
  }
}
