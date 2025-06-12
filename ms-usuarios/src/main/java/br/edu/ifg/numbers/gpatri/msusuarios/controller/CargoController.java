package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.service.CargoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        CargoResponseDTO novoCargo = cargoService.criarCargo(cargoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCargo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> buscarPorId(@PathVariable UUID id) {
        CargoResponseDTO cargo = cargoService.buscarPorId(id);
        return ResponseEntity.ok(cargo);
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDTO>> buscarTodos() {
        List<CargoResponseDTO> cargos = cargoService.buscarTodos();
        return ResponseEntity.ok(cargos);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CargoResponseDTO> atualizarCargo(@PathVariable UUID id, @RequestBody @Valid CargoRequestDTO cargoRequestDTO) {
        CargoResponseDTO cargoAtualizado = cargoService.atualizarCargo(id, cargoRequestDTO);
        return ResponseEntity.ok(cargoAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarCargo(@PathVariable UUID id) {
        cargoService.deletarCargo(id);
    }
}
