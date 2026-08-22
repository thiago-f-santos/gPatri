package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.PermissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/permissoes")
@RequiredArgsConstructor
@Tag(name = "Permissão", description = "Endpoints relacionados ao gerenciamento e consulta de permissões")
public class PermissaoController {

    private final PermissaoService permissaoService;

    @Operation(summary = "Retorna uma lista de permissões, opcionalmente filtrada por categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de permissões retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSAO_LISTAR')")
    public ResponseEntity<List<PermissaoResponseDTO>> buscarTodas(@RequestParam(required = false) String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return ResponseEntity.ok(permissaoService.buscarPorCategoria(categoria));
        }
        return ResponseEntity.ok(permissaoService.buscarTodas());
    }

    @Operation(summary = "Retorna uma permissão por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissão retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Permissão não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSAO_LISTAR')")
    public ResponseEntity<PermissaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        PermissaoResponseDTO permissao = permissaoService.findById(id);
        return ResponseEntity.ok(permissao);
    }
}
