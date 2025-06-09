package br.edu.ifg.numbers.msusuarios.controller;

import br.edu.ifg.numbers.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.msusuarios.service.CargoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CargoResponseDTO> criarCargo(@RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        try {
            CargoResponseDTO novoCargo = cargoService.criarCargo(cargoRequestDTO);
            return ResponseEntity.status(201).body(novoCargo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CargoResponseDTO(null, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> buscarPorId(@PathVariable UUID id) {
        try {
            CargoResponseDTO cargo = cargoService.buscarPorId(id);
            return ResponseEntity.ok(cargo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDTO>> buscarTodos() {
        List<CargoResponseDTO> cargos = cargoService.buscarTodos();
        return ResponseEntity.ok(cargos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> atualizarCargo(@PathVariable UUID id, @RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        try {
            CargoResponseDTO cargoAtualizado = cargoService.atualizarCargo(id, cargoRequestDTO);
            return ResponseEntity.ok(cargoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CargoResponseDTO(null, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCargo(@PathVariable UUID id) {
        try {
            cargoService.deletarCargo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
