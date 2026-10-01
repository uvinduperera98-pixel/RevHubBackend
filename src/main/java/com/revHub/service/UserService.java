package com.revHub.service;

import com.revHub.dto.request.ChangePasswordRequestDTO;
import com.revHub.dto.request.UserModifyRequestDTO;
import com.revHub.dto.request.UserSaveRequestDTO;
import com.revHub.dto.request.UserSearchRequestDTO;
import com.revHub.dto.response.UserIdNameResponseDto;
import com.revHub.dto.response.UserTableViewResponseDTO;
import com.revHub.entity.User;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User saveUserDetails(UserSaveRequestDTO dto);

    Page<UserTableViewResponseDTO> searchUserSummaries(UserSearchRequestDTO request, Pageable pageable);

    UserTableViewResponseDTO getUserById(Long userId);

    void updateUser(UserModifyRequestDTO dto);

    List<UserIdNameResponseDto> getAllUserNameList();

    void changePassword(ChangePasswordRequestDTO dto);
}
