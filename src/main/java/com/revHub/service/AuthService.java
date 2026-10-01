package com.revHub.service;

import com.revHub.dto.request.LoginRequestDTO;
import com.revHub.dto.response.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

}
