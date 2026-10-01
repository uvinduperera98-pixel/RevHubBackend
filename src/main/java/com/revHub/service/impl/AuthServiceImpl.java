package com.revHub.service.impl;

import com.revHub.dto.request.LoginRequestDTO;
import com.revHub.dto.response.LoginResponseDTO;
import com.revHub.dto.response.RoleNameResponseDTO;
import com.revHub.entity.User;
import com.revHub.repository.UserRepository;
import com.revHub.security.JwtTokenProvider;
import com.revHub.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Check whether the account is active
        if (!user.isActive()) {
            throw new RuntimeException("Your account has been deactivated. Please contact the administrator.");
        }

        // Fixed password check: added '!' so it throws an error if passwords DO NOT match
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token using the full user object (embeds uid & fullName)
        String token = jwtTokenProvider.generateToken(user);

        // Build response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        List<RoleNameResponseDTO> roles = user.getRoles()
                .stream()
                .map(role -> new RoleNameResponseDTO(
                        role.getRoleId(),
                        role.getRoleName()
                ))
                .toList();

        response.setRole(roles);
        return response;
    }
}