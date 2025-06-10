package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Tag(name = "Categoria", description = "Endpoints relacionados ao gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Operation(summary = "Salva uma categoria no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria salva com sucesso"),
            @ApiResponse(responseCode = "400", description = "Nome da categoria vazio"),
            @ApiResponse(responseCode = "404", description = "Categoria mãe não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> save(@RequestBody @Valid CategoriaCreateDTO categoriaDTO) {
        CategoriaResponseDTO categoriaResponseDTO = categoriaService.save(categoriaDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoriaResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(categoriaResponseDTO);
    }

    @Operation(summary = "Retorna uma lista com todas as Categorias presentes no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        List<CategoriaResponseDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok().body(categorias);
    }

    @Operation(summary = "Retorna uma categoria encontrada por meio do seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable UUID id) {
        CategoriaResponseDTO categoria = categoriaService.findById(id);
        return ResponseEntity.ok().body(categoria);
    }

    @Operation(summary = "Atualiza uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid CategoriaUpdateDTO categoriaUpdateDTO) {
        CategoriaResponseDTO categoriaAtualizada = categoriaService.update(id, categoriaUpdateDTO);
        return ResponseEntity.ok().body(categoriaAtualizada);
    }

    @Operation(summary = "Deleta uma categoria pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
