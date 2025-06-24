package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.LoginRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.LoginResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthService authService;

    @Operation(summary = "Autentica um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos de login inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> autenticarUsuario(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.autenticarUsuario(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }

}
