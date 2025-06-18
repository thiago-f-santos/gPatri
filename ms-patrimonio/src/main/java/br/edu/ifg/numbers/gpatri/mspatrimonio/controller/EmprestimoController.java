package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> save(@RequestBody @Valid EmprestimoCreateDTO emprestimoCreateDTO) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.save(emprestimoCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(emprestimoResponseDTO.getId()).toUri();
        return ResponseEntity.created(location).body(emprestimoResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid EmprestimoUpdateDTO emprestimoUpdateDTO) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.update(id, emprestimoUpdateDTO);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        emprestimoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> findEmprestimoById(@PathVariable UUID id) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.findById(id);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<EmprestimoResponseDTO> aprovar(@PathVariable UUID id) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.aprovarEmprestimo(id);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }

    @PatchMapping("/{id}/negar")
    public ResponseEntity<EmprestimoResponseDTO> negar(@PathVariable UUID id) {
        EmprestimoResponseDTO emprestimoResponseDTO = emprestimoService.negarEmprestimo(id);
        return ResponseEntity.ok(emprestimoResponseDTO);
    }
}
