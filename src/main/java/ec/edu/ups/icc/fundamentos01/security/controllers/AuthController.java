package ec.edu.ups.icc.fundamentos01.security.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ec.edu.ups.icc.fundamentos01.security.dtos.RefreshTokenRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.AuthResponseDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.LoginRequestDto;
import ec.edu.ups.icc.fundamentos01.security.dtos.RegisterRequestDto;
import ec.edu.ups.icc.fundamentos01.security.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")   
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login - Endpoint público (configurado en SecurityConfig)
     * POST /auth/login
     */

    @Operation(summary = "Iniciar sesión", description = "Permite a un usuario iniciar sesión y obtener un token JWT.")
    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, devuelve el token JWT.")
    @ApiResponse(responseCode = "409", description = "El usuario no está activo, no puede iniciar sesión.")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas, no autorizado.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        AuthResponseDto response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Registro - Endpoint público (configurado en SecurityConfig)
     * POST /auth/register
     */
    @Operation(summary = "Registrar usuario", description = "Permite registrar un nuevo usuario en el sistema.")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente, devuelve el token JWT.")
    @ApiResponse(responseCode = "409", description = "El email ya está registrado.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        AuthResponseDto response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Refrescar token", description = "Permite obtener un nuevo token JWT a partir de un refresh token válido.")
    @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente, devuelve el nuevo token JWT.")
    @ApiResponse(responseCode = "401", description = "El refresh token no es válido, ya expiró o fue revocado.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        AuthResponseDto response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token del usuario, cerrando la sesión.")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada exitosamente.")
    @ApiResponse(responseCode = "401", description = "El refresh token no es válido, ya expiró o fue revocado.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequestDto request) {
        authService.logout(request);
    }
}
