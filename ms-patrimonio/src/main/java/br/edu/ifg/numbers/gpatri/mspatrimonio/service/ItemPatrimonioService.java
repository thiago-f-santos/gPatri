package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Condicao;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.CondicaoMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.ItemPatrimonioMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.PatrimonioMapper;
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
public class ItemPatrimonioService {

    private final ItemPatrimonioRepository itemPatrimonioRepository;
    private final ItemPatrimonioMapper itemPatrimonioMapper;

    private final PatrimonioService patrimonioService;
    private final PatrimonioMapper patrimonioMapper;

    private final CondicaoService condicaoService;
    private final CondicaoMapper condicaoMapper;

    @Transactional
    public ItemPatrimonioResponseDTO save(ItemPatrimonioCreateDTO itemPatrimonioCreateDTO) {
        ItemPatrimonio itemPatrimonio = new ItemPatrimonio();
        Patrimonio patrimonio = patrimonioMapper.responseDtoToPatrimonio(patrimonioService.findById(itemPatrimonioCreateDTO.getIdPatrimonio()));
        Condicao condicao = condicaoMapper.responseDtoToCondicao(condicaoService.findById(itemPatrimonioCreateDTO.getIdCondicao()));
        itemPatrimonio.setPatrimonio(patrimonio);
        itemPatrimonio.setCondicao(condicao);
        itemPatrimonio.setCreatedAt(Instant.now());
        itemPatrimonio = itemPatrimonioRepository.save(itemPatrimonio);
        return itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio);
    }

    @Transactional
    public ItemPatrimonioResponseDTO update(UUID id, ItemPatrimonioUpdateDTO itemPatrimonioUpdateDTO) {
        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item não encontrado"));
        if (itemPatrimonioUpdateDTO.getIdPatrimonio() != null) {
            Patrimonio patrimonio = patrimonioMapper.responseDtoToPatrimonio(patrimonioService.findById(itemPatrimonioUpdateDTO.getIdPatrimonio()));
            itemPatrimonio.setPatrimonio(patrimonio);
        }
        if (itemPatrimonioUpdateDTO.getIdCondicao() != null) {
            Condicao condicao = condicaoMapper.responseDtoToCondicao(condicaoService.findById(itemPatrimonioUpdateDTO.getIdCondicao()));
            itemPatrimonio.setCondicao(condicao);
        }
        itemPatrimonio.setEmUso(itemPatrimonioUpdateDTO.isEmUso());
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

    public List<ItemPatrimonioResponseDTO> findAllByPatrimonioId(UUID patrimonioId) {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllByPatrimonioId(patrimonioId);
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

    public List<ItemPatrimonioResponseDTO> findAllByCondicaoId(UUID condicaoId) {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllByCondicaoId(condicaoId);
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

    public List<ItemPatrimonioResponseDTO> findAllByEmUsoEquals(boolean emUso) {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllByEmUsoEquals(emUso);
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

}