package com.pororoz.istock.domain.user.service;

import com.pororoz.istock.domain.user.dto.service.DeleteUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.SaveUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UpdateUserServiceRequest;
import com.pororoz.istock.domain.user.dto.service.UserServiceResponse;
import com.pororoz.istock.domain.user.entity.Role;
import com.pororoz.istock.domain.user.entity.User;
import com.pororoz.istock.domain.user.exception.RoleNotFoundException;
import com.pororoz.istock.domain.user.exception.SelfDeleteAccountException;
import com.pororoz.istock.domain.user.exception.SelfDemoteRoleException;
import com.pororoz.istock.domain.user.exception.UserNotFoundException;
import com.pororoz.istock.domain.user.repository.RoleRepository;
import com.pororoz.istock.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object myPrincipal = auth.getPrincipal();
    Object myRole = auth.getAuthorities().iterator().next().toString();

    Role role = roleRepository.findByRoleName(updateUserServiceRequest.getRoleName())
        .orElseThrow(RoleNotFoundException::new);
    User targetUser = userRepository.findById(updateUserServiceRequest.getUserId())
        .orElseThrow(UserNotFoundException::new);
    String encodedPassword = passwordEncoder.encode(updateUserServiceRequest.getPassword());

    if (myPrincipal.equals(targetUser.getUsername())
        && myRole.equals("ROLE_ADMIN")
        && role.getRoleName().equals("ROLE_USER")) {
      throw new SelfDemoteRoleException();
    }

    targetUser.update(encodedPassword, role);
    return UserServiceResponse.of(targetUser);
  }

  public UserServiceResponse deleteUser(DeleteUserServiceRequest deleteUserServiceRequest) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object myPrincipal = auth.getPrincipal();

    User user = userRepository.findById(deleteUserServiceRequest.getUserId())
        .orElseThrow(UserNotFoundException::new);

    if (myPrincipal.equals(user.getUsername())) {
      throw new SelfDeleteAccountException();
    }
    userRepository.delete(user);

    return UserServiceResponse.of(user);
  }

  public UserServiceResponse saveUser(SaveUserServiceRequest saveUserServiceRequest) {
    Role role = roleRepository.findByRoleName(saveUserServiceRequest.getRoleName())
        .orElseThrow(RoleNotFoundException::new);
    String encodedPassword = passwordEncoder.encode(saveUserServiceRequest.getPassword());
    User user = saveUserServiceRequest.toUser(encodedPassword, role);
    User result = userRepository.save(user);
    return UserServiceResponse.of(result);
  }

  @Transactional(readOnly = true)
  public Page<UserServiceResponse> findUsers(Pageable pageable) {
    return userRepository.findAll(pageable).map(UserServiceResponse::of);
  }
}
