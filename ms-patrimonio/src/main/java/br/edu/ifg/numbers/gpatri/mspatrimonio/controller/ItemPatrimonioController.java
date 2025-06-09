package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.ItemPatrimonioService;
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
@RequestMapping("/api/v1/itenspatrimonio")
@RequiredArgsConstructor
public class ItemPatrimonioController {

    private final ItemPatrimonioService itemPatrimonioService;

    @PostMapping
    public ResponseEntity<ItemPatrimonioResponseDTO> save(@RequestBody @Valid ItemPatrimonioCreateDTO itemPatrimonioCreateDTO){
        ItemPatrimonioResponseDTO itemPatrimonioResponseDTO = itemPatrimonioService.save(itemPatrimonioCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(itemPatrimonioResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(itemPatrimonioResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPatrimonioResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid ItemPatrimonioUpdateDTO itemPatrimonioUpdateDTO) {
        ItemPatrimonioResponseDTO itemPatrimonioResponseDTO = itemPatrimonioService.update(id, itemPatrimonioUpdateDTO);
        return ResponseEntity.ok().body(itemPatrimonioResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ItemPatrimonioResponseDTO> delete(@PathVariable UUID id) {
        itemPatrimonioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPatrimonioResponseDTO> findById(@PathVariable UUID id) {
        ItemPatrimonioResponseDTO itemPatrimonioResponseDTO = itemPatrimonioService.findById(id);
        return ResponseEntity.ok().body(itemPatrimonioResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ItemPatrimonioResponseDTO>> findAll() {
        List<ItemPatrimonioResponseDTO> itensPatrimonio = itemPatrimonioService.findAll();
        return ResponseEntity.ok().body(itensPatrimonio);
    }

}
