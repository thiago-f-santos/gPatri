package br.edu.ifg.numbers.msusuarios.controller;

import br.edu.ifg.numbers.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //Rota para criar um novo usuário
    @PostMapping
    public ResponseEntity<UserResponseDTO> criarUsuario(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        try {
            UserResponseDTO novoUsuario = usuarioService.criarUsuario(userRequestDTO);
            return ResponseEntity.status(201).body(novoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new UserResponseDTO(null, e.getMessage(), null, null, null));
        }
    }

    // Rota para buscar um usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable UUID id) {
        try {
            UserResponseDTO usuario = usuarioService.buscarPid(id);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Rota para buscar todos os usuários
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> buscarTodos() {
        List<UserResponseDTO> usuarios = usuarioService.buscarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UserRequestDTO userRequestDTO) {
        try {
            UserResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, userRequestDTO);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new UserResponseDTO(null, e.getMessage(), null, null, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID id) {
        try {
            usuarioService.deletarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
