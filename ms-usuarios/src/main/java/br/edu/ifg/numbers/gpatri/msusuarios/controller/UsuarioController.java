package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //Rota para criar um novo usuário
    @PostMapping
    public ResponseEntity<UserResponseDTO> criarUsuario(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        UserResponseDTO novoUsuario = usuarioService.criarUsuario(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // Rota para buscar um usuário pelo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIO')")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable UUID id) {
        UserResponseDTO usuario = usuarioService.buscarPid(id);
        return ResponseEntity.ok(usuario);
    }

    // Rota para buscar todos os usuários
    @GetMapping
    @PreAuthorize("hasAuthority('LISTAR_USUARIOS')")
    public ResponseEntity<List<UserResponseDTO>> buscarTodos() {
        List<UserResponseDTO> usuarios = usuarioService.buscarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_USUARIO')")
    public ResponseEntity<UserResponseDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, userUpdateDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EXCLUIR_USUARIO')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable UUID id) {
        usuarioService.deletarUsuario(id);
    }
}
