package com.revHub.controller;

import com.revHub.dto.request.*;
import com.revHub.dto.request.UserModifyRequestDTO;
import com.revHub.dto.request.UserSaveRequestDTO;
import com.revHub.dto.request.UserSearchRequestDTO;
import com.revHub.dto.response.UserIdNameResponseDto;
import com.revHub.dto.response.UserTableViewResponseDTO;
import com.revHub.entity.User;
import com.revHub.service.UserService;
import com.revHub.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveUserDetails(@Valid @RequestBody UserSaveRequestDTO userSaveRequestDTO) {
        User savedUser = userService.saveUserDetails(userSaveRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(new StandardResponse(
                HttpStatus.CREATED.value(),
                "User saved successfully",
                savedUser
        ));
    }

    @PostMapping("/search")
    public ResponseEntity<StandardResponse> searchUserSummaries(
            @RequestBody UserSearchRequestDTO request,
            @PageableDefault(page = 0, size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserTableViewResponseDTO> responseDtoPage = userService.searchUserSummaries(request, pageable);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Users retrieved successfully",
                responseDtoPage
        ));
    }

    @GetMapping("/get-user-by-userId/{userId}")
    public ResponseEntity<StandardResponse> getUserById(@PathVariable Long userId) {
        UserTableViewResponseDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(), "Success", user));
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateUser(@RequestBody UserModifyRequestDTO dto) {
        userService.updateUser(dto);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "User updated successfully",
                null
        ));
    }

    @GetMapping("/get-all-user-names")
    public ResponseEntity<StandardResponse> getAllUserNameList() {
        List<UserIdNameResponseDto> userList = userService.getAllUserNameList();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                userList
        ));
    }

    // New change password endpoint
    @PutMapping("/change-password")
    public ResponseEntity<StandardResponse> changePassword(@RequestBody ChangePasswordRequestDTO dto) {
        userService.changePassword(dto);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Password changed successfully",
                null
        ));
    }
}
