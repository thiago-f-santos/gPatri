package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.CategoriaService;
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
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> save(@RequestBody CategoriaCreateDTO categoriaDTO) {
        CategoriaResponseDTO categoriaResponseDTO = categoriaService.save(categoriaDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoriaResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(categoriaResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        List<CategoriaResponseDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok().body(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable UUID id) {
        CategoriaResponseDTO categoria = categoriaService.findById(id);
        return ResponseEntity.ok().body(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable UUID id, @RequestBody CategoriaUpdateDTO categoriaUpdateDTO) {
        CategoriaResponseDTO categoriaAtualizada = categoriaService.update(id, categoriaUpdateDTO);
        return ResponseEntity.ok().body(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
