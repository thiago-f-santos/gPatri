package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.ItemEmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/itensemprestimo")
@RequiredArgsConstructor
@Tag(name = "Item Emprestimo", description = "Endpoints relacionados ao gerenciamentos de Itens de um Emprestimo")
public class ItemEmprestimoController {

    private final ItemEmprestimoService itemEmprestimoService;

    @Operation(summary = "Adiciona um Item ao empréstimo indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item adicionado ao empréstimo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ATUALIZAR_TODOS_EMPRESTIMOS') or (hasAuthority('ATUALIZAR_EMPRESTIMO') and @emprestimoService.isOwner(#idEmprestimo, authentication.principal.id))")
    @PostMapping("/adicionar/{idEmprestimo}")
    public ResponseEntity<ItemEmprestimoResponseDTO> adicionaItemEmprestimo(@PathVariable UUID idEmprestimo, @RequestBody ItemEmprestimoCreateDTO itemEmprestimoCreateDTO) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemEmprestimoResponseDTO);
    }

    @Operation(summary = "Atualiza um Item do empréstimo indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item adicionado ao empréstimo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado no empréstimo indicado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ATUALIZAR_TODOS_EMPRESTIMOS') or (hasAuthority('ATUALIZAR_EMPRESTIMO') and @emprestimoService.isOwner(#idEmprestimo, authentication.principal.id))")
    @PutMapping("/modificar/{idEmprestimo}")
    public ResponseEntity<ItemEmprestimoResponseDTO> modificarItemEmprestimo(@PathVariable UUID idEmprestimo, @RequestBody ItemEmprestimoUpdateDTO itemEmprestimoUpdateDTO) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = itemEmprestimoService.atualizaItemEmprestimo(idEmprestimo, itemEmprestimoUpdateDTO);
        return  ResponseEntity.ok(itemEmprestimoResponseDTO);
    }

    @Operation(summary = "Remove um Item do empréstimo indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removido do empréstimo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado no empréstimo indicado"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ATUALIZAR_TODOS_EMPRESTIMOS') or (hasAuthority('ATUALIZAR_EMPRESTIMO') and @emprestimoService.isOwner(#idEmprestimo, authentication.principal.id))")
    @DeleteMapping("/remover/{idEmprestimo}/{idItemPatrimonio}")
    public ResponseEntity<Void> removeItemEmprestimo(@PathVariable UUID idEmprestimo, @PathVariable UUID idItemPatrimonio) {
        itemEmprestimoService.apagarItemEmprestimo(idEmprestimo, idItemPatrimonio);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retorna um Item de Emprestimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item Emprestimo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado no empréstimo indicado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('VISUALIZAR_TODOS_EMPRESTIMOS') or (hasAuthority('VISUALIZAR_EMPRESTIMO') and @emprestimoService.isOwner(#idEmprestimo, authentication.principal.id))")
    @GetMapping("/{idEmprestimo}/{idItemPatrimonio}")
    public ResponseEntity<ItemEmprestimoResponseDTO> findByIdEmprestimoIdItemPatrimonio(@PathVariable UUID idEmprestimo, @PathVariable UUID idItemPatrimonio) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = itemEmprestimoService.findById(idEmprestimo, idItemPatrimonio);
        return ResponseEntity.ok(itemEmprestimoResponseDTO);
    }

    @Operation(summary = "Retorna todos Itens do Emprestimo indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('VISUALIZAR_TODOS_EMPRESTIMOS') or (hasAuthority('VISUALIZAR_EMPRESTIMO') and @emprestimoService.isOwner(#idEmprestimo, authentication.principal.id))")
    @GetMapping("/{idEmprestimo}")
    public ResponseEntity<List<ItemEmprestimoResponseDTO>> findByIdEmprestimo(@PathVariable UUID idEmprestimo) {
        List<ItemEmprestimoResponseDTO> itensEmprestimos = itemEmprestimoService.findAllByEmprestimo(idEmprestimo);
        return ResponseEntity.ok(itensEmprestimos);
    }
}
