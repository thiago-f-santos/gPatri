package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Emprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimoId;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.ItemEmUsoException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeItemIndisponivelException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.SituacaoEmprestimoInvalidaException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.ItemEmprestimoMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.EmprestimoRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemEmprestimoRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemPatrimonioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemEmprestimoService {

    private final ItemEmprestimoRepository itemEmprestimoRepository;
    private final ItemEmprestimoMapper itemEmprestimoMapper;

    private final EmprestimoRepository emprestimoRepository;

    private final ItemPatrimonioRepository itemPatrimonioRepository;

    @Transactional
    public ItemEmprestimoResponseDTO adicionaItemEmprestimo(UUID idEmprestimo, ItemEmprestimoCreateDTO itemEmprestimoCreateDTO) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimo.getSituacao() == SituacaoEmprestimo.NEGADO || emprestimo.getSituacao() == SituacaoEmprestimo.DEVOLVIDO)
            throw new SituacaoEmprestimoInvalidaException(String.format("O empréstimo '%s' não pode ser atualizado pois foi negado ou devolvido.", idEmprestimo));

        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(itemEmprestimoCreateDTO.getIdItemPatrimonio()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item Patrimonio '%s' não encontrado", itemEmprestimoCreateDTO.getIdItemPatrimonio())));

        if (itemPatrimonio.getQuantidade() == 0) throw new ItemEmUsoException(String.format("Item Patrimonio '%s' ja se encontra em uso", itemEmprestimoCreateDTO.getIdItemPatrimonio()));

        if (itemPatrimonio.getQuantidade() < itemEmprestimoCreateDTO.getQuantidade()) throw new QuantidadeItemIndisponivelException(String.format("Quantidade disponivel do item patrimonio '%s' é menor que quantidade solicitada", itemEmprestimoCreateDTO.getIdItemPatrimonio()));

//        emprestimo.setSituacao(SituacaoEmprestimo.EM_ESPERA);
//        emprestimo.setIdUsuarioAvaliador(null);
//        emprestimo.setUpdatedAt(Instant.now());
//        emprestimoRepository.save(emprestimo);

        itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() - itemEmprestimoCreateDTO.getQuantidade());
        itemPatrimonio.setUpdatedAt(Instant.now());
        itemPatrimonioRepository.save(itemPatrimonio);

        ItemEmprestimoId itemEmprestimoId = new ItemEmprestimoId();
        itemEmprestimoId.setIdEmprestimo(emprestimo.getId());
        itemEmprestimoId.setIdItemPatrimonio(itemPatrimonio.getId());

        ItemEmprestimo itemEmprestimo = itemEmprestimoMapper.createDtoToItemEmprestimo(itemEmprestimoCreateDTO);
        itemEmprestimo.setId(itemEmprestimoId);
        itemEmprestimo.setItemPatrimonio(itemPatrimonio);
        itemEmprestimo.setEmprestimo(emprestimo);
        itemEmprestimo.setCreatedAt(Instant.now());
        itemEmprestimoRepository.save(itemEmprestimo);

        return itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(itemEmprestimo);
    }

    @Transactional
    public ItemEmprestimoResponseDTO atualizaItemEmprestimo(UUID idEmprestimo, ItemEmprestimoUpdateDTO itemEmprestimoUpdateDTO) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimo.getSituacao() == SituacaoEmprestimo.NEGADO || emprestimo.getSituacao() == SituacaoEmprestimo.DEVOLVIDO)
            throw new SituacaoEmprestimoInvalidaException(String.format("O empréstimo '%s' não pode ser atualizado pois foi negado ou devolvido.", idEmprestimo));

        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(itemEmprestimoUpdateDTO.getIdItemPatrimonio()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item Patrimonio '%s' não encontrado", itemEmprestimoUpdateDTO.getIdItemPatrimonio())));

        ItemEmprestimo itemEmprestimo = itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(emprestimo.getId(), itemPatrimonio.getId());

        if (itemEmprestimo == null) throw new EntityNotFoundException(String.format("Item patrimonio '%s' não encontrado no emprestimo '%s'", itemPatrimonio.getId(), emprestimo.getId()));

        if (itemPatrimonio.getQuantidade() == 0) throw new ItemEmUsoException(String.format("Item Patrimonio '%s' ja se encontra em uso", itemEmprestimoUpdateDTO.getIdItemPatrimonio()));

        if (itemPatrimonio.getQuantidade() < itemEmprestimoUpdateDTO.getQuantidade()) throw new QuantidadeItemIndisponivelException(String.format("Quantidade disponivel do item patrimonio '%s' é menor que quantidade solicitada", itemEmprestimoUpdateDTO.getIdItemPatrimonio()));

        int diff = itemEmprestimo.getQuantidade() - itemEmprestimoUpdateDTO.getQuantidade();

//        emprestimo.setSituacao(SituacaoEmprestimo.EM_ESPERA);
//        emprestimo.setIdUsuarioAvaliador(null);
//        emprestimo.setUpdatedAt(Instant.now());
//        emprestimoRepository.save(emprestimo);

        itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() + diff);
        itemPatrimonio.setUpdatedAt(Instant.now());
        itemPatrimonioRepository.save(itemPatrimonio);

        itemEmprestimo.setQuantidade(itemEmprestimo.getQuantidade() + itemEmprestimoUpdateDTO.getQuantidade());
        itemEmprestimo.setUpdatedAt(Instant.now());
        itemEmprestimo = itemEmprestimoRepository.save(itemEmprestimo);

        return itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(itemEmprestimo);
    }

    @Transactional
    public void apagarItemEmprestimo(UUID idEmprestimo, UUID idItemPatrimonio) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimo.getSituacao() == SituacaoEmprestimo.NEGADO || emprestimo.getSituacao() == SituacaoEmprestimo.DEVOLVIDO)
            throw new SituacaoEmprestimoInvalidaException(String.format("O empréstimo '%s' não pode ser atualizado pois foi negado ou devolvido.", idEmprestimo));

        ItemEmprestimo itemEmprestimo = itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio);

        if (itemEmprestimo == null)
            throw new EntityNotFoundException(String.format("Item Patrimonio '%s' no Emprestimo '%s' não encontrado", idItemPatrimonio, idEmprestimo));

        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(idItemPatrimonio).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item Patrimonio '%s' não encontrado", idItemPatrimonio)));

        itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() + itemEmprestimo.getQuantidade());
        itemPatrimonio.setUpdatedAt(Instant.now());
        itemPatrimonioRepository.save(itemPatrimonio);

        itemEmprestimoRepository.delete(itemEmprestimo);
    }

    public ItemEmprestimoResponseDTO findById(UUID idEmprestimo, UUID idItemPatrimonio) {
        ItemEmprestimo itemEmprestimo = itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio);
        if (itemEmprestimo == null)
            throw new EntityNotFoundException(String.format("Item Emprestimo ItemPatrimonio<'%s'> no Emprestimo<'%s'> não encontrado", idItemPatrimonio, idEmprestimo));

        return itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(itemEmprestimo);
    }

    public List<ItemEmprestimoResponseDTO> findAllByEmprestimo(UUID idEmprestimo) {
        List<ItemEmprestimo> itensEmprestimo = itemEmprestimoRepository.findAllByEmprestimo_IdEquals(idEmprestimo);
        return itensEmprestimo.stream().map(itemEmprestimoMapper::itemEmprestimoToItemEmprestimoResponseDTO).toList();
    }

}