package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.*;
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

    @PutMapping("/{id}/atualizar")
    public ResponseEntity<ItemEmprestimoResponseDTO> atualizaItemEmprestimo(@PathVariable UUID id, @RequestBody @Valid ItemEmprestimoUpdateDTO itemEmprestimoUpdateDTO) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = emprestimoService.atualizaItemEmprestimo(id, itemEmprestimoUpdateDTO);
        return ResponseEntity.ok(itemEmprestimoResponseDTO);
    }

//    @DeleteMapping("/{idEmprestimo}/remover/{idItemPatrimonio}")
//    public ResponseEntity<Void> removeItemEmprestimo(@PathVariable UUID idEmprestimo, @PathVariable UUID idItemPatrimonio) {
//        ItemEmprestimoId itemEmprestimoId = new ItemEmprestimoId();
//        itemEmprestimoId.setIdItemPatrimonio(idItemPatrimonio);
//        itemEmprestimoId.setIdEmprestimo(idEmprestimo);
//        emprestimoService.removeItemEmprestimo(itemEmprestimoId);
//        return ResponseEntity.noContent().build();
//    }

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

}
