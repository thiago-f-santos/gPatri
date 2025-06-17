package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Emprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.ItemEmUsoException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeItemIndisponivelException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.ItemEmprestimoMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.EmprestimoRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemEmprestimoRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemPatrimonioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemEmprestimoService {

    private final ItemEmprestimoRepository itemEmprestimoRepository;
    private final ItemEmprestimoMapper itemEmprestimoMapper;

    private final EmprestimoRepository emprestimoRepository;

    private final ItemPatrimonioRepository itemPatrimonioRepository;

    @Transactional
    public ItemEmprestimoResponseDTO adicionaItemEmprestimo() {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(itemEmprestimoCreateDTO.getIdItemPatrimonio()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item Patrimonio '%s' não encontrado", itemEmprestimoCreateDTO.getIdItemPatrimonio())));

        if (itemPatrimonio.getQuantidade() == 0) throw new ItemEmUsoException(String.format("Item Patrimonio '%s' ja se encontra em uso", itemEmprestimoCreateDTO.getIdItemPatrimonio()));

        if (itemPatrimonio.getQuantidade() < itemEmprestimoCreateDTO.getQuantidade()) throw new QuantidadeItemIndisponivelException(String.format("Quantidade disponivel do item patrimonio '%s' é menor que quantidade solicitada", itemEmprestimoCreateDTO.getIdItemPatrimonio()));

        ItemEmprestimo itemEmprestimo = itemEmprestimoMapper.createDtoToItemEmprestimo(itemEmprestimoCreateDTO);
        itemEmprestimo.setItemPatrimonio(itemPatrimonio);
        itemEmprestimo.setEmprestimo(emprestimo);
        itemEmprestimo.setCreatedAt(Instant.now());

        itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() - itemEmprestimoCreateDTO.getQuantidade());
        itemPatrimonioRepository.save(itemPatrimonio);

        itemEmprestimoRepository.save(itemEmprestimo);

        return itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(itemEmprestimo);
    }

    @Transactional
    public ItemEmprestimoResponseDTO atualizaItemEmprestimo(UUID idEmprestimo, ItemEmprestimoUpdateDTO itemEmprestimoUpdateDTO) {

    }

}
