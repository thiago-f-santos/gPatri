package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.ItemPatrimonioMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemPatrimonioRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.PatrimonioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemPatrimonioService {

    private final ItemPatrimonioRepository itemPatrimonioRepository;
    private final ItemPatrimonioMapper itemPatrimonioMapper;

    private final PatrimonioRepository patrimonioRepository;

    @Transactional
    public ItemPatrimonioResponseDTO save(ItemPatrimonioCreateDTO itemPatrimonioCreateDTO) {
        ItemPatrimonio itemPatrimonio = itemPatrimonioMapper.createDtoToItemPatrimonio(itemPatrimonioCreateDTO);
        Patrimonio patrimonio = patrimonioRepository.findById(itemPatrimonioCreateDTO.getIdPatrimonio()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Patrimonio '%s' não encontrado", itemPatrimonioCreateDTO.getIdPatrimonio())));
        itemPatrimonio.setPatrimonio(patrimonio);
        itemPatrimonio.setCreatedAt(Instant.now());
        itemPatrimonio = itemPatrimonioRepository.save(itemPatrimonio);
        return itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio);
    }

    @Transactional
    public ItemPatrimonioResponseDTO update(UUID id, ItemPatrimonioUpdateDTO itemPatrimonioUpdateDTO) {
        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item não encontrado"));
        if (itemPatrimonioUpdateDTO.getIdPatrimonio() != null) {
            Patrimonio patrimonio = patrimonioRepository.findById(itemPatrimonioUpdateDTO.getIdPatrimonio()).orElseThrow(
                    () -> new EntityNotFoundException(String.format("Patrimonio '%s' não encontrado", itemPatrimonioUpdateDTO.getIdPatrimonio())));
            itemPatrimonio.setPatrimonio(patrimonio);
        }
        if (itemPatrimonioUpdateDTO.getCondicaoProduto() != null) {
            itemPatrimonio.setCondicaoProduto(itemPatrimonioUpdateDTO.getCondicaoProduto());
        }
        if (itemPatrimonioUpdateDTO.getCondicaoDescricao() != null) {
            itemPatrimonio.setCondicaoDescricao(itemPatrimonioUpdateDTO.getCondicaoDescricao());
        }
        if (itemPatrimonioUpdateDTO.getQuantidade() != null) {
            itemPatrimonio.setQuantidade(itemPatrimonio.getQuantidade());
        }

        itemPatrimonio.setUpdatedAt(Instant.now());
        itemPatrimonio = itemPatrimonioRepository.save(itemPatrimonio);
        return itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio);
    }

    @Transactional
    public void deleteById(UUID id) {
        if(!itemPatrimonioRepository.existsById(id)) {
            throw new EntityNotFoundException("Item não encontrado");
        }
        itemPatrimonioRepository.deleteById(id);
    }

    public ItemPatrimonioResponseDTO findById(UUID id) {
        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));
        return itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio);
    }

    public List<ItemPatrimonioResponseDTO> findAll() {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAll();
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

}