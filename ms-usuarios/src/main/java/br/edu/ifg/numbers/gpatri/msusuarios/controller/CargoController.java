package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.CargoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cargos")
@Tag(name = "Cargo", description = "Endpoints relacionados ao gerenciamento de cargos")
public class CargoController {

    private final CargoService cargoService;

    @Autowired
    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @Operation(summary = "Salva um novo cargo no banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cargo salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para criação"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CARGO_CADASTRAR')")
    public ResponseEntity<CargoResponseDTO> criarCargo(@RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        CargoResponseDTO novoCargo = cargoService.criarCargo(cargoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCargo);
    }

    @Operation(summary = "Retorna um cargo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cargo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CARGO_LISTAR')")
    public ResponseEntity<CargoResponseDTO> buscarPorId(@PathVariable UUID id) {
        CargoResponseDTO cargo = cargoService.buscarPorId(id);
        return ResponseEntity.ok(cargo);
    }

    @Operation(summary = "Retorna uma lista de cargos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cargos retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('CARGO_LISTAR')")
    public ResponseEntity<List<CargoResponseDTO>> buscarTodos() {
        List<CargoResponseDTO> cargos = cargoService.buscarTodos();
        return ResponseEntity.ok(cargos);
    }

    @Operation(summary = "Atualiza um cargo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Campos inválidos para atualização"),
            @ApiResponse(responseCode = "404", description = "Cargo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('CARGO_EDITAR')")
    public ResponseEntity<CargoResponseDTO> atualizarCargo(@PathVariable UUID id, @RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        CargoResponseDTO cargoAtualizado = cargoService.atualizarCargo(id, cargoRequestDTO);
        return ResponseEntity.ok(cargoAtualizado);
    }

    @Operation(summary = "Deleta um cargo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cargo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cargo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado, usuário não possui permissão"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CARGO_EXCLUIR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarCargo(@PathVariable UUID id) {
        cargoService.deletarCargo(id);
    }
}
