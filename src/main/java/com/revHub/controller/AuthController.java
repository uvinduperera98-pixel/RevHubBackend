package com.revHub.controller;

import com.revHub.dto.request.LoginRequestDTO;
import com.revHub.dto.response.LoginResponseDTO;
import com.revHub.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(
        origins = {"http://localhost:4200", "https://rev-hub-admin-glqa.vercel.app"},
        allowedHeaders = {"Content-Type","Authorization"}, methods = {RequestMethod.POST, RequestMethod.GET})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
