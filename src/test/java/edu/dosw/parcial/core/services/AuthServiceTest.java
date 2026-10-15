package edu.dosw.parcial.core.services;

import edu.dosw.parcial.config.JwtTokenProvider;
import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.core.models.Rol;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void loginSuccessReturnsValidTokenAndRole() {
        LoginRequestDTO request = new LoginRequestDTO("cliente@gmail.com", "Password123");
        UsuarioDocument user = new UsuarioDocument("usr-1", "Carlos", "cliente@gmail.com", "3101234567", "hashEncoded", Rol.CLIENTE);

        when(usuarioRepository.findByCorreo("cliente@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123", "hashEncoded")).thenReturn(true);
        when(jwtTokenProvider.createToken("cliente@gmail.com", Rol.CLIENTE)).thenReturn("mock.jwt.token");
        when(jwtTokenProvider.getExpirationMillis()).thenReturn(86400000L);

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals("CLIENTE", response.getRol());
        assertEquals(86400000L, response.getExpiracionMilis());
        verify(usuarioRepository).findByCorreo("cliente@gmail.com");
    }

    @Test
    void loginFailsWhenUserNotFoundThrowsException() {
        LoginRequestDTO request = new LoginRequestDTO("inexistente@gmail.com", "Password123");

        when(usuarioRepository.findByCorreo("inexistente@gmail.com")).thenReturn(Optional.empty());

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> authService.login(request));

        assertEquals("E401-AUTH", ex.getCodigo());
        assertEquals("Credenciales invalidas", ex.getMessage());
    }

    @Test
    void loginFailsWhenPasswordIncorrectThrowsException() {
        LoginRequestDTO request = new LoginRequestDTO("cliente@gmail.com", "WrongPassword");
        UsuarioDocument user = new UsuarioDocument("usr-1", "Carlos", "cliente@gmail.com", "3101234567", "hashEncoded", Rol.CLIENTE);

        when(usuarioRepository.findByCorreo("cliente@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashEncoded")).thenReturn(false);

        ValidationBusinessException ex = assertThrows(ValidationBusinessException.class, () -> authService.login(request));

        assertEquals("E401-AUTH", ex.getCodigo());
        assertEquals("Credenciales invalidas", ex.getMessage());
    }
}
