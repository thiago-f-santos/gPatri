package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.CondicaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping(value = "/api/v1/condicoes")
@RequiredArgsConstructor
@Tag(name = "Condição", description = "Endpoints relacionados ao gerenciamento de condições")
public class CondicaoController {

    private final CondicaoService condicaoService;

    @Operation(summary = "Salva uma condição no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Condição criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para a criação"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PostMapping
    public ResponseEntity<CondicaoResponseDTO> save(@RequestBody @Valid CondicaoCreateDTO condicaoCreateDTO) {
        CondicaoResponseDTO condicaoResponseDTO = condicaoService.save(condicaoCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(condicaoResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(condicaoResponseDTO);
    }

    @Operation(summary = "Atualiza uma condição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Condição atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para a atualização"),
            @ApiResponse(responseCode = "404", description = "Condição não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid CondicaoUpdateDTO condicaoUpdateDTO) {
        CondicaoResponseDTO condicaoResponseDTO = condicaoService.update(id, condicaoUpdateDTO);
        return ResponseEntity.ok().body(condicaoResponseDTO);
    }

    @Operation(summary = "Deleta uma condição")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Condição deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Condição não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> delete(@PathVariable UUID id) {
        condicaoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retorna uma condição por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Condição retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Condição não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> findById(@PathVariable UUID id) {
        CondicaoResponseDTO condicaoResponseDTO = condicaoService.findById(id);
        return ResponseEntity.ok().body(condicaoResponseDTO);
    }

    @Operation(summary = "Retorna uma lista de condições")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping
    public ResponseEntity<List<CondicaoResponseDTO>> findAll() {
        List<CondicaoResponseDTO> condicoes = condicaoService.findAll();
        return ResponseEntity.ok().body(condicoes);
    }

}
