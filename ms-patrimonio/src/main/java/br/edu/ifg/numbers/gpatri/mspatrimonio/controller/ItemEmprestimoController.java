package br.edu.ifg.numbers.gpatri.mspatrimonio.controller;

import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.service.ItemEmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/v1/itensemprestimo")
@RequiredArgsConstructor
public class ItemEmprestimoController {

    private final ItemEmprestimoService itemEmprestimoService;

    @PostMapping("/adicionar/{idEmprestimo}")
    public ResponseEntity<ItemEmprestimoResponseDTO> adicionaItemEmprestimo(@PathVariable UUID idEmprestimo, @RequestBody ItemEmprestimoCreateDTO itemEmprestimoCreateDTO) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemEmprestimoResponseDTO);
    }

    @PutMapping("/modificar/{idEmprestimo}")
    public ResponseEntity<ItemEmprestimoResponseDTO> modificarItemEmprestimo(@PathVariable UUID idEmprestimo, @RequestBody ItemEmprestimoUpdateDTO itemEmprestimoUpdateDTO) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = itemEmprestimoService.atualizaItemEmprestimo(idEmprestimo, itemEmprestimoUpdateDTO);
        return  ResponseEntity.ok(itemEmprestimoResponseDTO);
    }

    @DeleteMapping("/remover/{idEmprestimo}/{idItemPatrimonio}")
    public ResponseEntity<Void> removeItemEmprestimo(@PathVariable UUID idEmprestimo, @PathVariable UUID idItemPatrimonio) {
        itemEmprestimoService.apagarItemEmprestimo(idEmprestimo, idItemPatrimonio);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idEmprestimo}/{idItemPatrimonio}")
    public ResponseEntity<ItemEmprestimoResponseDTO> findByIdEmprestimoIdItemPatrimonio(@PathVariable UUID idEmprestimo, @PathVariable UUID idItemPatrimonio) {
        ItemEmprestimoResponseDTO itemEmprestimoResponseDTO = itemEmprestimoService.findById(idEmprestimo, idItemPatrimonio);
        return ResponseEntity.ok(itemEmprestimoResponseDTO);
    }

    @GetMapping("/{idEmprestimo}")
    public ResponseEntity<List<ItemEmprestimoResponseDTO>> findByIdEmprestimo(@PathVariable UUID idEmprestimo) {
        List<ItemEmprestimoResponseDTO> itensEmprestimos = itemEmprestimoService.findAllByEmprestimo(idEmprestimo);
        return ResponseEntity.ok(itensEmprestimos);
    }
}
