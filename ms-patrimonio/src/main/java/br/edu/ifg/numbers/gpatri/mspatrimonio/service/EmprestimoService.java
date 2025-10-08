package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.*;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.EmprestimoVazioException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.SituacaoEmprestimoInvalidaException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.EmprestimoMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.ItemEmprestimoMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.EmprestimoRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemEmprestimoRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemPatrimonioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

        if (emprestimoCreateDTO.getItensEmprestimo() == null)
            throw new EmprestimoVazioException("O emprestimo precisa de itens para ser criado");

        List<ItemEmprestimoCreateDTO> itensEmprestimoDTO = emprestimoCreateDTO.getItensEmprestimo();
        List<UUID> idsItensPatrimonio = emprestimoCreateDTO.getItensEmprestimo().stream().map(ItemEmprestimoCreateDTO::getIdItemPatrimonio).toList();
        Map<UUID, ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllById(idsItensPatrimonio)
                .stream().collect(Collectors.toMap(ItemPatrimonio::getId, i -> i));
        List<ItemEmprestimo> itensEmprestimo = new LinkedList<>();

        for (ItemEmprestimoCreateDTO itemEmprestimoDTO : itensEmprestimoDTO) {
            ItemPatrimonio itemPatrimonio = itensPatrimonio.get(itemEmprestimoDTO.getIdItemPatrimonio());
            if (itemPatrimonio == null) throw new EntityNotFoundException(
                    String.format("Item Patrimonio '%s' não encontrado", itemEmprestimoDTO.getIdItemPatrimonio())
            );

            itemPatrimonio.removeUnits(itemEmprestimoDTO.getQuantidade());

            ItemEmprestimoId itemEmprestimoId = new ItemEmprestimoId();
            itemEmprestimoId.setIdEmprestimo(emprestimo.getId());
            itemEmprestimoId.setIdItemPatrimonio(itemPatrimonio.getId());

            ItemEmprestimo itemEmprestimo = itemEmprestimoMapper.createDtoToItemEmprestimo(itemEmprestimoDTO);
            itemEmprestimo.setId(itemEmprestimoId);
            itemEmprestimo.setEmprestimo(emprestimo);
            itemEmprestimo.setItemPatrimonio(itemPatrimonio);
            itemEmprestimo.setQuantidade(itemEmprestimoDTO.getQuantidade());
            itemEmprestimo.setCreatedAt(Instant.now());
            itensEmprestimo.add(itemEmprestimo);
        }

        itemPatrimonioRepository.saveAll(itensPatrimonio.values());
        itemEmprestimoRepository.saveAll(itensEmprestimo);
        emprestimo.getItensEmprestimo().addAll(itensEmprestimo);
        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    @Transactional
    public EmprestimoResponseDTO update(UUID idEmprestimo, EmprestimoUpdateDTO emprestimoUpdateDTO) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        if (emprestimoUpdateDTO.getDataEmprestimo() != null)
            emprestimo.setDataEmprestimo(emprestimoUpdateDTO.getDataEmprestimo());

        if (emprestimoUpdateDTO.getDataDevolucao() != null)
            emprestimo.setDataDevolucao(emprestimoUpdateDTO.getDataDevolucao());

        emprestimo.setUpdatedAt(Instant.now());
        emprestimo = emprestimoRepository.save(emprestimo);

        return emprestimoMapper.emprestimoToEmprestimoResponseDto(emprestimo);
    }

    @Transactional
    public void delete(UUID idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));

        List<ItemEmprestimo> itemEmprestimos = itemEmprestimoRepository.findAllByEmprestimo_IdEquals(emprestimo.getId());
        if (itemEmprestimos.isEmpty()) {
            emprestimoRepository.delete(emprestimo);
            return;
        }

        List<UUID> idsItemPatrimonio = itemEmprestimos.stream().map(itemEmprestimo -> itemEmprestimo.getItemPatrimonio().getId()).toList();

        Map<UUID, ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllById(idsItemPatrimonio)
                .stream().collect(Collectors.toMap(ItemPatrimonio::getId, itemPatrimonio -> itemPatrimonio));

        itemEmprestimos.forEach(itemEmprestimo -> {
            ItemPatrimonio itemPatrimonio = itensPatrimonio.get(itemEmprestimo.getItemPatrimonio().getId());
            if (itemPatrimonio == null) throw new EntityNotFoundException(
                    String.format("Item Patrimonio '%s' não encontrado", itemEmprestimo.getItemPatrimonio().getId())
            );
            itemPatrimonio.addUnits(itemEmprestimo.getQuantidade());
        });

        itemPatrimonioRepository.saveAll(itensPatrimonio.values());
        itemEmprestimoRepository.deleteAll(itemEmprestimos);
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

        if (emprestimo.getSituacao() != SituacaoEmprestimo.APROVADO && emprestimo.getSituacao() != SituacaoEmprestimo.ATRASADO)
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

    public Page<EmprestimoResponseDTO> findAll(Pageable pageable) {
        Page<Emprestimo> emprestimos = emprestimoRepository.findAll(pageable);
        return emprestimos.map(emprestimoMapper::emprestimoToEmprestimoResponseDto);
    }

    public Page<EmprestimoResponseDTO> findAllBySituacaoEmprestimo(SituacaoEmprestimo situacaoEmprestimo, Pageable pageable) {
        Page<Emprestimo> emprestimos = emprestimoRepository.findAllBySituacaoEquals(situacaoEmprestimo, pageable);
        return emprestimos.map(emprestimoMapper::emprestimoToEmprestimoResponseDto);
    }

    public Page<EmprestimoResponseDTO> findAllByUserId(UUID userId, Pageable pageable) {
        Page<Emprestimo> emprestimos = emprestimoRepository.findAllByIdUsuarioEquals(userId, pageable);
        return emprestimos.map(emprestimoMapper::emprestimoToEmprestimoResponseDto);
    }

    public boolean isSelf(UUID idClaim, UUID idUsuario) {
        return idClaim.equals(idUsuario);
    }

    public boolean isOwner(UUID idEmprestimo, UUID idUsuario) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo).orElseThrow(
                () -> new EntityNotFoundException(String.format("Emprestimo '%s' não encontrado", idEmprestimo)));
        return emprestimo.getIdUsuario().equals(idUsuario);
    }

    private void devolveItensEmprestimo(Emprestimo emprestimo) {
        List<ItemEmprestimo> itensEmprestimo = itemEmprestimoRepository.findAllByEmprestimo_IdEquals(emprestimo.getId());
        if (itensEmprestimo.isEmpty()) {
            log.info("Emprestimo '{}' não possui itens a ser devolvidos.", emprestimo.getId());
            return;
        }

        List<UUID> idsPatrimonios = itensEmprestimo.stream().map(itemEmprestimo -> itemEmprestimo.getItemPatrimonio().getId()).toList();

        Map<UUID, ItemPatrimonio> itensPatrimonioMap = itemPatrimonioRepository.findAllById(idsPatrimonios)
                .stream().collect(Collectors.toMap(ItemPatrimonio::getId, itemPatrimonio -> itemPatrimonio));

        itensEmprestimo.forEach(itemEmprestimo -> {
            ItemPatrimonio itemPatrimonio = itensPatrimonioMap.get(itemEmprestimo.getItemPatrimonio().getId());
            if (itemPatrimonio == null) throw new EntityNotFoundException(
                    String.format("Item Patrimonio '%s' não encontrado", itemEmprestimo.getItemPatrimonio().getId())
            );
            itemPatrimonio.addUnits(itemEmprestimo.getQuantidade());
        });

        itemPatrimonioRepository.saveAll(itensPatrimonioMap.values());
        log.info("Devolvidas as unidades de {} itens de patrimonio do empréstimo '{}'",  itensPatrimonioMap.size(), emprestimo.getId());
    }

    @Scheduled(cron = "1 0 0 * * *", zone = "America/Sao_Paulo")
    public void atualizaEmprestimos() {
        List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findAllBySituacaoEqualsAndDataDevolucaoBefore(SituacaoEmprestimo.APROVADO, Date.from(Instant.now()));
        if (emprestimosAtrasados.isEmpty()) {
            log.info("Não há nenhum empréstimo a ser marcado como atrasado.");
        }

        final Instant now = Instant.now();

        log.info("Foram encontrados {} empréstimos para serem marcados como atrasados.", emprestimosAtrasados.size());
        emprestimosAtrasados.forEach(emprestimo -> {
           emprestimo.setSituacao(SituacaoEmprestimo.ATRASADO);
           emprestimo.setUpdatedAt(now);
        });

        emprestimoRepository.saveAll(emprestimosAtrasados);
        log.info("{} empréstimos atrasados foram atualizados com a situação {}.", emprestimosAtrasados.size(), SituacaoEmprestimo.ATRASADO);
    }

}