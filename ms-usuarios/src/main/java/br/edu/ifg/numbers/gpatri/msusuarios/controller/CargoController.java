package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.CargoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cargos")
public class CargoController {

    private final CargoService cargoService;

    @Autowired
    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRAR_CARGO')")
    public ResponseEntity<CargoResponseDTO> criarCargo(@RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        CargoResponseDTO novoCargo = cargoService.criarCargo(cargoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCargo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VISUALIZAR_CARGO')")
    public ResponseEntity<CargoResponseDTO> buscarPorId(@PathVariable UUID id) {
        CargoResponseDTO cargo = cargoService.buscarPorId(id);
        return ResponseEntity.ok(cargo);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LISTAR_CARGOS')")
    public ResponseEntity<List<CargoResponseDTO>> buscarTodos() {
        List<CargoResponseDTO> cargos = cargoService.buscarTodos();
        return ResponseEntity.ok(cargos);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_CARGO')")
    public ResponseEntity<CargoResponseDTO> atualizarCargo(@PathVariable UUID id, @RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        CargoResponseDTO cargoAtualizado = cargoService.atualizarCargo(id, cargoRequestDTO);
        return ResponseEntity.ok(cargoAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EXCLUIR_CARGO')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarCargo(@PathVariable UUID id) {
        cargoService.deletarCargo(id);
    }
}
