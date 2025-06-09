package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.CondicaoService;
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
public class CondicaoController {

    private final CondicaoService condicaoService;

    @PostMapping
    public ResponseEntity<CondicaoResponseDTO> save(@RequestBody @Valid CondicaoCreateDTO condicaoCreateDTO) {
        CondicaoResponseDTO condicaoResponseDTO = condicaoService.save(condicaoCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(condicaoResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(condicaoResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid CondicaoUpdateDTO condicaoUpdateDTO) {
        CondicaoResponseDTO condicaoResponseDTO = condicaoService.update(id, condicaoUpdateDTO);
        return ResponseEntity.ok().body(condicaoResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> delete(@PathVariable UUID id) {
        condicaoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CondicaoResponseDTO> findById(@PathVariable UUID id) {
        CondicaoResponseDTO condicaoResponseDTO = condicaoService.findById(id);
        return ResponseEntity.ok().body(condicaoResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CondicaoResponseDTO>> findAll() {
        List<CondicaoResponseDTO> condicoes = condicaoService.findAll();
        return ResponseEntity.ok().body(condicoes);
    }

}
