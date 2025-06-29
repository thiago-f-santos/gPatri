package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.security.JwtAuthenticationFilter;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Emprestimo", description = "Endpoints relacionados ao gerenciamento de emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @Operation(summary = "Salva um emprestimo e seus itens de empréstimo iniciais no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Emprestimo salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos invalidos"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_SOLICITAR')")
    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> save(@RequestBody @Valid EmprestimoCreateDTO emprestimoCreateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtAuthenticationFilter.CustomUserDetails userDetails) {
            userId = userDetails.getId();
        } else {
            throw new IllegalStateException("Usuário não autenticado ou principal inválido (esperado CustomUserDetails).");
        }

        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.save(userId, emprestimoCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(emprestimoResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(emprestimoResponseDTO);
    }

    @Operation(summary = "Atualiza um emprestimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprestimo atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_EDITAR_TODOS') or (hasAuthority('EMPRESTIMO_EDITAR') and @emprestimoService.isOwner(#id, authentication.principal.id))")
    @PutMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid EmprestimoUpdateDTO emprestimoUpdateDTO) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.update(id, emprestimoUpdateDTO);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }

    @Operation(summary = "Deleta um emprestimo e devolve itens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Emprestimo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_EXCLUIR_TODOS') or (hasAuthority('EMPRESTIMO_EXCLUIR') and @emprestimoService.isOwner(#id, authentication.principal.id))")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        emprestimoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retorna um emprestimo pelo seu id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprestimo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_LISTAR_TODOS') or (hasAuthority('EMPRESTIMO_LISTAR') and @emprestimoService.isOwner(#id, authentication.principal.id))")
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> findEmprestimoById(@PathVariable UUID id) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.findById(id);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }

    @Operation(summary = "Retorna uma lista com todos os empréstimos encontrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_LISTAR_TODOS')")
    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> findAll() {
        List<EmprestimoResponseDTO> emprestimos = emprestimoService.findAll();
        return ResponseEntity.ok(emprestimos);
    }

    @Operation(summary = "Aprova um emprestimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprestimo aprovado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_LIBERAR')")
    @PatchMapping("/{idEmprestimo}/aprovar")
    public ResponseEntity<EmprestimoResponseDTO> aprovar(@PathVariable UUID idEmprestimo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtAuthenticationFilter.CustomUserDetails userDetails) {
            userId = userDetails.getId();
        } else {
            throw new IllegalStateException("Usuário não autenticado ou principal inválido (esperado CustomUserDetails).");
        }

        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.aprovarEmprestimo(userId, idEmprestimo);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }

    @Operation(summary = "Nega um emprestimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Emprestimo negado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "404", description = "Item Patrimonio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('EMPRESTIMO_LIBERAR')")
    @PatchMapping("/{idEmprestimo}/negar")
    public ResponseEntity<EmprestimoResponseDTO> negar(@PathVariable UUID idEmprestimo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof JwtAuthenticationFilter.CustomUserDetails userDetails) {
            userId = userDetails.getId();
        } else {
            throw new IllegalStateException("Usuário não autenticado ou principal inválido (esperado CustomUserDetails).");
        }

        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.negarEmprestimo(userId, idEmprestimo);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }
}
