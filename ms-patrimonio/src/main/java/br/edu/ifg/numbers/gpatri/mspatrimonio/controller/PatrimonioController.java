package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.PatrimonioService;
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
@RequestMapping("/api/v1/patrimonios")
@RequiredArgsConstructor
public class PatrimonioController {

    private final PatrimonioService patrimonioService;

    @PostMapping
    public ResponseEntity<PatrimonioResponseDTO> save(@RequestBody @Valid PatrimonioCreateDTO patrimonioCreateDTO) {
        PatrimonioResponseDTO patrimonioResponseDTO = patrimonioService.save(patrimonioCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(patrimonioResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(patrimonioResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatrimonioResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid PatrimonioUpdateDTO patrimonioUpdateDTO) {
        PatrimonioResponseDTO patrimonioResponseDTO = patrimonioService.update(id, patrimonioUpdateDTO);
        return ResponseEntity.ok().body(patrimonioResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        patrimonioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatrimonioResponseDTO> findById(@PathVariable UUID id) {
        PatrimonioResponseDTO patrimonioResponseDTO = patrimonioService.findById(id);
        return ResponseEntity.ok().body(patrimonioResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PatrimonioResponseDTO>> findAll() {
        List<PatrimonioResponseDTO> patrimonioResponseDTO = patrimonioService.findAll();
        return ResponseEntity.ok().body(patrimonioResponseDTO);
    }

}
