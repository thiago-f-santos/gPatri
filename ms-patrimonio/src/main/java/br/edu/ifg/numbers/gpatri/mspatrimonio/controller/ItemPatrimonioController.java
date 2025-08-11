package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.ItemPatrimonioService;
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
@RequestMapping("/api/v1/itenspatrimonio")
@RequiredArgsConstructor
@Tag(name = "Item Patrimônio", description = "Endpoints relacionados ao gerenciamento de itens de um determinado patrimônio")
public class ItemPatrimonioController {

    private final ItemPatrimonioService itemPatrimonioService;

    @Operation(summary = "Salva um item patrimônio no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item Patrimonio salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para criação"),
            @ApiResponse(responseCode = "404", description = "Patrimônio ou Condição não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_CADASTRAR')")
    @PostMapping
    public ResponseEntity<ItemPatrimonioResponseDTO> save(@RequestBody @Valid ItemPatrimonioCreateDTO itemPatrimonioCreateDTO){
        ItemPatrimonioResponseDTO itemPatrimonioResponseDTO = itemPatrimonioService.save(itemPatrimonioCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(itemPatrimonioResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(itemPatrimonioResponseDTO);
    }

    @Operation(summary = "Atualiza um item patrimônio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item Patrimonio atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item Patrimônio / Patrimônio / Condição não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_EDITAR')")
    @PutMapping("/{id}")
    public ResponseEntity<ItemPatrimonioResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid ItemPatrimonioUpdateDTO itemPatrimonioUpdateDTO) {
        ItemPatrimonioResponseDTO itemPatrimonioResponseDTO = itemPatrimonioService.update(id, itemPatrimonioUpdateDTO);
        return ResponseEntity.ok().body(itemPatrimonioResponseDTO);
    }

    @Operation(summary = "Deleta um item patrimônio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item Patrimonio deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item Patrimônio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_EXCLUIR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ItemPatrimonioResponseDTO> delete(@PathVariable UUID id) {
        itemPatrimonioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Retorna um item patrimônio por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item Patrimonio retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item Patrimônio não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_LISTAR')")
    @GetMapping("/{id}")
    public ResponseEntity<ItemPatrimonioResponseDTO> findById(@PathVariable UUID id) {
        ItemPatrimonioResponseDTO itemPatrimonioResponseDTO = itemPatrimonioService.findById(id);
        return ResponseEntity.ok().body(itemPatrimonioResponseDTO);
    }

    @Operation(summary = "Retorna uma lista com todos item patrimônio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item Patrimonio retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_LISTAR')")
    @GetMapping
    public ResponseEntity<List<ItemPatrimonioResponseDTO>> findAll() {
        List<ItemPatrimonioResponseDTO> itensPatrimonio = itemPatrimonioService.findAll();
        return ResponseEntity.ok().body(itensPatrimonio);
    }

    @Operation(summary = "Retorna uma lista com todos item patrimônio filtrados pelo nome da categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item Patrimonio retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_LISTAR')")
    @GetMapping("/categoria")
    public ResponseEntity<List<ItemPatrimonioResponseDTO>> findAllByCategoriaName(@RequestParam String nomeCategoria) {
        List<ItemPatrimonioResponseDTO> itensPatrimonio = itemPatrimonioService.findAllByCategoriaName(nomeCategoria);
        return ResponseEntity.ok().body(itensPatrimonio);
    }

    @Operation(summary = "Retorna uma lista com todos item patrimônio filtrados pelo patrimônio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item Patrimonio retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PreAuthorize("hasAuthority('ITEM_PATRIMONIO_LISTAR')")
    @GetMapping("/patrimonio")
    public ResponseEntity<List<ItemPatrimonioResponseDTO>> findAllByPatrimonio(@RequestParam(required = false) UUID idPatrimonio,
                                                                               @RequestParam(required = false) String nomePatrimonio) {
        List<ItemPatrimonioResponseDTO> itensPatrimonio;
        if (idPatrimonio != null) {
            itensPatrimonio= itemPatrimonioService.findAllByPatrimonioId(idPatrimonio);
        } else if (nomePatrimonio != null) {
            itensPatrimonio= itemPatrimonioService.findAllByPatrimonioName(nomePatrimonio);
        } else return ResponseEntity.badRequest().body(null);

        return ResponseEntity.ok().body(itensPatrimonio);
    }

}
