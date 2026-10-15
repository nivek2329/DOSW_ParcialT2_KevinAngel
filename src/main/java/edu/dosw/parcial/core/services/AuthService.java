package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
}
