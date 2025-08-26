package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Emprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimoId;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.EmprestimoVazioException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.ItemEmUsoException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeItemIndisponivelException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.SituacaoEmprestimoInvalidaException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.EmprestimoMapper;
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
public class EmprestimoService {

    private final EmprestimoRepository  emprestimoRepository;
    private final EmprestimoMapper emprestimoMapper;

    private final ItemEmprestimoRepository itemEmprestimoRepository;
    private final ItemEmprestimoMapper itemEmprestimoMapper;

    private final ItemPatrimonioRepository itemPatrimonioRepository;

    @Transactional
    public EmprestimoResponseDTO save(UUID idUsuario, EmprestimoCreateDTO emprestimoCreateDTO) {
        Emprestimo emprestimo = emprestimoMapper.createDtoToEmprestimo(emprestimoCreateDTO);
        emprestimo.setIdUsuario(idUsuario);
        emprestimo.setSituacao(SituacaoEmprestimo.EM_ESPERA);
        emprestimo.setCreatedAt(Instant.now());
        emprestimo = emprestimoRepository.save(emprestimo);

        if (emprestimoCreateDTO.getItensEmprestimo() != null) {
            for (ItemEmprestimoCreateDTO itemEmprestimoCreateDTO : emprestimoCreateDTO.getItensEmprestimo()) {
                ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(itemEmprestimoCreateDTO.getIdItemPatrimonio())
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Item patrimonio '%s' nao encontrado", itemEmprestimoCreateDTO.getIdItemPatrimonio())));

                if (itemPatrimonio.getQuantidade() == 0) {
                    throw new ItemEmUsoException(String.format("O item '%s' ja se encontra em uso", itemPatrimonio.getId()));
                }

                if (itemPatrimonio.getQuantidade() < itemEmprestimoCreateDTO.getQuantidade()) {
                    throw new QuantidadeItemIndisponivelException(String.format("A quantidade solicitada do item '%s' é maior que a quantidade em estoque", itemPatrimonio.getId()));
                }

                itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() - itemEmprestimoCreateDTO.getQuantidade());
                itemPatrimonio.setUpdatedAt(Instant.now());
                itemPatrimonioRepository.save(itemPatrimonio);

                ItemEmprestimoId itemEmprestimoId = new ItemEmprestimoId();
                itemEmprestimoId.setIdEmprestimo(emprestimo.getId());
                itemEmprestimoId.setIdItemPatrimonio(itemPatrimonio.getId());

                ItemEmprestimo itemEmprestimo = itemEmprestimoMapper.createDtoToItemEmprestimo(itemEmprestimoCreateDTO);
                itemEmprestimo.setId(itemEmprestimoId);
                itemEmprestimo.setEmprestimo(emprestimo);
                itemEmprestimo.setItemPatrimonio(itemPatrimonio);
                itemEmprestimo.setQuantidade(itemEmprestimoCreateDTO.getQuantidade());
                itemEmprestimo.setCreatedAt(Instant.now());
                itemEmprestimoRepository.save(itemEmprestimo);

                emprestimo.getItensEmprestimo().add(itemEmprestimo);
            }
        } else throw new EmprestimoVazioException("O emprestimo precisa de itens para ser criado");

        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO update(UUID idEmprestimo, EmprestimoUpdateDTO emprestimoUpdateDTO) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimoUpdateDTO.getDataEmprestimo() != null) {
            emprestimo.setDataEmprestimo(emprestimoUpdateDTO.getDataEmprestimo());
        }
        if (emprestimoUpdateDTO.getDataDevolucao() != null) {
            emprestimo.setDataDevolucao(emprestimoUpdateDTO.getDataDevolucao());
        }

        emprestimo.setUpdatedAt(Instant.now());
        emprestimo = emprestimoRepository.save(emprestimo);

        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    @Transactional
    public void delete(UUID idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        List<ItemEmprestimo> itemEmprestimos = itemEmprestimoRepository.findAllByEmprestimo_IdEquals(emprestimo.getId());

        if (emprestimo.getSituacao() != SituacaoEmprestimo.DEVOLVIDO && emprestimo.getSituacao() != SituacaoEmprestimo.NEGADO) {
            itemEmprestimos.forEach(itemEmprestimo -> {
                ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(itemEmprestimo.getItemPatrimonio().getId())
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Item Patrimonio '%s' não encontrado", itemEmprestimo.getItemPatrimonio().getId())));
                itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() + itemEmprestimo.getQuantidade());
                itemPatrimonioRepository.save(itemPatrimonio);
                itemEmprestimoRepository.delete(itemEmprestimo);
            });
        } else {
            itemEmprestimoRepository.deleteAll(itemEmprestimos);
        }

        emprestimoRepository.delete(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO aprovarEmprestimo(UUID idUsuarioAvaliador, UUID idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimo.getSituacao() != SituacaoEmprestimo.EM_ESPERA)
            throw new SituacaoEmprestimoInvalidaException(String.format("O empréstimo '%s' não está em espera", idEmprestimo));

        emprestimo.setIdUsuarioAvaliador(idUsuarioAvaliador);
        emprestimo.setSituacao(SituacaoEmprestimo.APROVADO);
        emprestimo.setUpdatedAt(Instant.now());
        emprestimo = emprestimoRepository.save(emprestimo);

        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO negarEmprestimo(UUID idUsuarioAvaliador, UUID idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimo.getSituacao() != SituacaoEmprestimo.EM_ESPERA)
            throw new SituacaoEmprestimoInvalidaException(String.format("O empréstimo '%s' não está em espera", idEmprestimo));

        emprestimo.setIdUsuarioAvaliador(idUsuarioAvaliador);
        emprestimo.setSituacao(SituacaoEmprestimo.NEGADO);
        emprestimo.setUpdatedAt(Instant.now());

        devolveItensEmprestimo(emprestimo);

        emprestimo = emprestimoRepository.save(emprestimo);

        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO devolverEmprestimo(UUID idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimo.getSituacao() != SituacaoEmprestimo.APROVADO)
            throw new SituacaoEmprestimoInvalidaException(String.format("O empréstimo '%s' não pode ser devolvido, situação invalida para devolução", idEmprestimo));

        emprestimo.setSituacao(SituacaoEmprestimo.DEVOLVIDO);
        emprestimo.setUpdatedAt(Instant.now());

        devolveItensEmprestimo(emprestimo);

        emprestimo = emprestimoRepository.save(emprestimo);

        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    public EmprestimoResponseDTO findById(UUID idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));
        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    public List<EmprestimoResponseDTO> findAll() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        return emprestimos.stream().map(emprestimoMapper::emprestimoToEmprestimoResponseDto).toList();
    }

    public boolean isOwner(UUID idEmprestimo, UUID idUsuario) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));
        return emprestimo.getIdUsuario().equals(idUsuario);
    }

    private void devolveItensEmprestimo(Emprestimo emprestimo) {
        List<ItemEmprestimo> itemEmprestimos = itemEmprestimoRepository.findAllByEmprestimo_IdEquals(emprestimo.getId());
        itemEmprestimos.forEach(itemEmprestimo -> {
            ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(itemEmprestimo.getItemPatrimonio().getId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Item Patrimonio '%s' não encontrado", itemEmprestimo.getItemPatrimonio().getId())));
            itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade() + itemEmprestimo.getQuantidade());
            itemPatrimonio.setUpdatedAt(Instant.now());
            itemPatrimonioRepository.save(itemPatrimonio);
        });
    }

    public List<EmprestimoResponseDTO> findAllBySituacaoEmprestimo(SituacaoEmprestimo situacaoEmprestimo) {
        List<Emprestimo> emprestimos = emprestimoRepository.findAllBySituacaoEquals(situacaoEmprestimo);
        return emprestimos.stream().map(emprestimoMapper::emprestimoToEmprestimoResponseDto).toList();
    }

    public List<EmprestimoResponseDTO> findAllByUserId(UUID userId) {
        List<Emprestimo> emprestimos = emprestimoRepository.findAllByIdUsuarioEquals(userId);
        return emprestimos.stream().map(emprestimoMapper::emprestimoToEmprestimoResponseDto).toList();
    }

    public boolean isSelf(UUID idClaim, UUID idUsuario) {
        return idClaim.equals(idUsuario);
    }
}