package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.PatrimonioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/patrimonios")
@RequiredArgsConstructor
@Tag(name = "Patrimônio", description = "Endpoints relacionados ao gerenciamento de patrimônios")
public class PatrimonioController {

    private final PatrimonioService patrimonioService;

    @Operation(summary = "Salva um patrimônio no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patrimonio salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para criação"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('PATRIMONIO_CADASTRAR')")
    @PostMapping
    public ResponseEntity<PatrimonioResponseDTO> save(@RequestBody @Valid PatrimonioCreateDTO patrimonioCreateDTO) {
        PatrimonioResponseDTO patrimonioResponseDTO = patrimonioService.save(patrimonioCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(patrimonioResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(patrimonioResponseDTO);
    }

    @Operation(summary = "Atualiza um patrimônio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patrimonio atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para atualização"),
            @ApiResponse(responseCode = "404", description = "Patrimônio ou Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('PATRIMONIO_EDITAR')")
    @PutMapping("/{id}")
    public ResponseEntity<PatrimonioResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid PatrimonioUpdateDTO patrimonioUpdateDTO) {
        PatrimonioResponseDTO patrimonioResponseDTO = patrimonioService.update(id, patrimonioUpdateDTO);
        return ResponseEntity.ok().body(patrimonioResponseDTO);
    }

    @Operation(summary = "Deleta um patrimônio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patrimonio deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Patrimônio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('PATRIMONIO_EXCLUIR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patrimonioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retorna um patrimônio por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patrimonio retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Patrimônio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('PATRIMONIO_LISTAR')")
    @GetMapping("/{id}")
    public ResponseEntity<PatrimonioResponseDTO> findById(@PathVariable UUID id) {
        PatrimonioResponseDTO patrimonioResponseDTO = patrimonioService.findById(id);
        return ResponseEntity.ok().body(patrimonioResponseDTO);
    }

    @Operation(summary = "Retorna uma lista de patrimônios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('PATRIMONIO_LISTAR')")
    @GetMapping
    public ResponseEntity<List<PatrimonioResponseDTO>> findAll() {
        List<PatrimonioResponseDTO> patrimonioResponseDTO = patrimonioService.findAll();
        return ResponseEntity.ok().body(patrimonioResponseDTO);
    }

}
