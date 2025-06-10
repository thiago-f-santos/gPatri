package br.edu.ifg.numbers.msusuarios.controller;

import br.edu.ifg.numbers.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserUpdateDTO;
import br.edu.ifg.numbers.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
        UserResponseDTO novoUsuario = usuarioService.criarUsuario(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    // Rota para buscar um usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable UUID id) {
        UserResponseDTO usuario = usuarioService.buscarPid(id);
        return ResponseEntity.ok(usuario);
    }

    // Rota para buscar todos os usuários
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> buscarTodos() {
        List<UserResponseDTO> usuarios = usuarioService.buscarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, userUpdateDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarUsuario(@PathVariable UUID id) {
        usuarioService.deletarUsuario(id);
    }
}
