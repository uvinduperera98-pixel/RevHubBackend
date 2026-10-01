package com.revHub.service.impl;

import com.revHub.dto.request.ChangePasswordRequestDTO;
import com.revHub.dto.request.UserModifyRequestDTO;
import com.revHub.dto.request.UserSaveRequestDTO;
import com.revHub.dto.request.UserSearchRequestDTO;
import com.revHub.dto.response.RoleNameResponseDTO;
import com.revHub.dto.response.UserIdNameResponseDto;
import com.revHub.dto.response.UserTableViewResponseDTO;
import com.revHub.entity.Role;
import com.revHub.entity.Technician;
import com.revHub.entity.User;
import com.revHub.entity.UserHistory;
import com.revHub.entity.enums.TechnicianStatus;
import com.revHub.exception.BadRequestException;
import com.revHub.exception.DuplicateException;
import com.revHub.exception.NotFoundException;
import com.revHub.repository.RoleRepository;
import com.revHub.repository.TechnicianRepository;
import com.revHub.repository.UserHistoryRepository;
import com.revHub.repository.UserRepository;
import com.revHub.service.UserService;
import com.revHub.util.StandardResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final TechnicianRepository technicianRepository;

    private final RoleRepository roleRepository;

    private final UserHistoryRepository userHistoryRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, TechnicianRepository technicianRepository, RoleRepository roleRepository, UserHistoryRepository userHistoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.technicianRepository = technicianRepository;
        this.roleRepository = roleRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User saveUserDetails(UserSaveRequestDTO dto) {

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setActive(true);
        user.setSpeciality(dto.getSpeciality());

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> fetchedRoles = roleRepository.findAllById(dto.getRoleIds());

            if (fetchedRoles.size() != dto.getRoleIds().size()) {
                throw new BadRequestException("One or more invalid Role IDs provided");
            }

            user.setRoles(new HashSet<>(fetchedRoles));
        }

        User savedUser = userRepository.save(user);

        boolean isTechnician = savedUser.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("TECHNICIAN"));

        if (isTechnician) {
            Technician technician = new Technician();
            technician.setUser(savedUser);
            technician.setTechnicianName(dto.getFullName());
            technician.setStatus(TechnicianStatus.AVAILABLE);

            technicianRepository.save(technician);
        }

        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserTableViewResponseDTO> searchUserSummaries(UserSearchRequestDTO request, Pageable pageable) {

        if (pageable.getSort().stream().anyMatch(order -> order.getProperty().equalsIgnoreCase("string"))) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdDate").descending()
            );
        }

        Page<User> userPage = userRepository.searchUsers(
                request.getUserId(),
                request.getRoleId(),
                request.getActiveStatus(),
                pageable
        );

        return userPage.map(user -> {
            List<RoleNameResponseDTO> roleDtos = user.getRoles().stream()
                    .map(role -> new RoleNameResponseDTO(role.getRoleId(), role.getRoleName()))
                    .collect(Collectors.toList());

            return new UserTableViewResponseDTO(
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getSpeciality(),
                    roleDtos,
                    user.isActive()
            );
        });
    }

    @Override
    public UserTableViewResponseDTO getUserById(Long userId) {
        User user = userRepository.findUserWithRolesById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<RoleNameResponseDTO> roleDtos = user.getRoles().stream()
                .map(role -> new RoleNameResponseDTO(role.getRoleId(), role.getRoleName()))
                .toList();

        return new UserTableViewResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getSpeciality(),
                roleDtos,
                user.isActive()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserModifyRequestDTO dto) {

        User user = userRepository.findUserWithRolesById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Save previous state before modifying
        UserHistory history = new UserHistory();
        history.setUserId(user.getUserId());
        history.setUsername(user.getUsername());
        history.setPassword(user.getPassword());
        history.setFullName(user.getFullName());
        history.setEmail(user.getEmail());
        history.setSpeciality(user.getSpeciality());
        history.setActive(user.isActive());
        history.setMustChangePassword(user.getMustChangePassword());
        history.setActionType("UPDATE");

        userHistoryRepository.save(history);

        // Fetch roles
        List<Role> rolesList = roleRepository.findAllById(dto.getRoleIds());

        if (rolesList.size() != dto.getRoleIds().size()) {
            throw new BadRequestException("One or more invalid Role IDs provided");
        }

        Set<Role> newRoles = new HashSet<>(rolesList);

        // Update user
        user.setActive(dto.getActive());
        user.setFullName(dto.getFullName());
        user.setSpeciality(dto.getSpeciality());

        // Update roles
        user.getRoles().clear();
        newRoles.forEach(user::addRole);

        userRepository.save(user);
    }

    @Override
    public List<UserIdNameResponseDto> getAllUserNameList() {
        return userRepository.findAllUserIdsAndNames().stream()
                .map(row -> new UserIdNameResponseDto((Long) row[0], (String) row[1]))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequestDTO dto) {

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        userRepository.save(user);
    }
}
