package edu.dosw.parcial.core.services;

import edu.dosw.parcial.config.JwtTokenProvider;
import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.controller.handlers.ValidationBusinessException;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Intento de inicio de sesion para usuario: {}", request.getCorreo());

        Optional<UsuarioDocument> userOpt = usuarioRepository.findByCorreo(request.getCorreo());
        if (userOpt.isEmpty()) {
            log.warn("Usuario no encontrado en base de datos: {}", request.getCorreo());
            throw new ValidationBusinessException("E401-AUTH", "Credenciales invalidas", HttpStatus.UNAUTHORIZED);
        }

        UsuarioDocument user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Contrasena incorrecta para usuario: {}", request.getCorreo());
            throw new ValidationBusinessException("E401-AUTH", "Credenciales invalidas", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.createToken(user.getCorreo(), user.getRol());
        log.info("Inicio de sesion exitoso para usuario: {}", user.getCorreo());

        return new LoginResponseDTO(token, "Bearer", user.getRol().name(), jwtTokenProvider.getExpirationMillis());
    }
}
