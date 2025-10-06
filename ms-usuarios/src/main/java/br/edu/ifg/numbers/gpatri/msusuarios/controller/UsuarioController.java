package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UsuarioCargoUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //Rota para criar um novo usuário
    @Operation(summary = "Salva um novo usuario no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para criação"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado"),
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> criarUsuario(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        UserResponseDTO novoUsuario = usuarioService.criarUsuario(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // Rota para buscar um usuário pelo ID
    @Operation(summary = "Retorna um usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario retornado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado"),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_LISTAR')")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable UUID id) {
        UserResponseDTO usuario = usuarioService.buscarPid(id);
        return ResponseEntity.ok(usuario);
    }

    // Rota para buscar todos os usuários
    @Operation(summary = "Retorna uma lista de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado"),
    })
    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_LISTAR')")
    public ResponseEntity<Page<UserResponseDTO>> buscarTodos(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDTO> usuarios = usuarioService.buscarTodos(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Atualiza um usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado"),
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_EDITAR')")
    public ResponseEntity<UserResponseDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, userUpdateDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @Operation(summary = "Deleta um usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado"),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_EXCLUIR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable UUID id) {
        usuarioService.deletarUsuario(id);
    }

    @PatchMapping("/{id}/cargo")
    @PreAuthorize("hasAuthority('CARGO_ATRIBUIR')")
    public ResponseEntity<UserResponseDTO> atribuirCargo(@PathVariable UUID id, @RequestBody @Valid UsuarioCargoUpdateDTO cargoId) {
        UserResponseDTO usuarioAtualizado = usuarioService.atribuirCargo(id, cargoId);
        return ResponseEntity.ok(usuarioAtualizado);
    }
}
