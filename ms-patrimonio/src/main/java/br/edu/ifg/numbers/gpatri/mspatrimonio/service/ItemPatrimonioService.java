package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.TipoControle;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeInvalidaException;
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

        if (patrimonio.getTipoControle().equals(TipoControle.UNITARIO) && itemPatrimonioCreateDTO.getQuantidade() > 1)
            throw new QuantidadeInvalidaException(String.format("Patrimonio '%s' é do tipo unitário, portanto a quantidade do item não pode ser maior que 1.", patrimonio.getId()));

        itemPatrimonio.setPatrimonio(patrimonio);
        itemPatrimonio.setCreatedAt(Instant.now());
        itemPatrimonio = itemPatrimonioRepository.save(itemPatrimonio);
        return itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio);
    }

    @Transactional
    public ItemPatrimonioResponseDTO update(UUID id, ItemPatrimonioUpdateDTO itemPatrimonioUpdateDTO) {
        ItemPatrimonio itemPatrimonio = itemPatrimonioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item não encontrado"));

        Patrimonio patrimonio = patrimonioRepository.findById(itemPatrimonioUpdateDTO.getIdPatrimonio()).orElseThrow(
                () -> new EntityNotFoundException(String.format("Patrimonio '%s' não encontrado", itemPatrimonioUpdateDTO.getIdPatrimonio())));

        if (itemPatrimonioUpdateDTO.getIdPatrimonio() != null) {
            itemPatrimonio.setPatrimonio(patrimonio);
        }
        if (itemPatrimonioUpdateDTO.getCondicaoProduto() != null) {
            itemPatrimonio.setCondicaoProduto(itemPatrimonioUpdateDTO.getCondicaoProduto());
        }
        if (itemPatrimonioUpdateDTO.getCondicaoDescricao() != null) {
            itemPatrimonio.setCondicaoDescricao(itemPatrimonioUpdateDTO.getCondicaoDescricao());
        }
        if (itemPatrimonioUpdateDTO.getQuantidade() != null) {
            if (patrimonio.getTipoControle().equals(TipoControle.UNITARIO) && itemPatrimonioUpdateDTO.getQuantidade() > 1)
                throw new QuantidadeInvalidaException(
                        String.format("Patrimonio '%s' é do tipo unitário, portanto a quantidade do item não pode ser maior que 1.", patrimonio.getId()
                        )
                );
            itemPatrimonio.setQuantidade(itemPatrimonioUpdateDTO.getQuantidade());
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

    public List<ItemPatrimonioResponseDTO> findAllByPatrimonioId(UUID patrimonioId) {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllByPatrimonioId(patrimonioId);
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

    public List<ItemPatrimonioResponseDTO> findAllByPatrimonioName(String nomePatrimonio) {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllByPatrimonio_NomeContainingIgnoreCase(nomePatrimonio);
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

    public List<ItemPatrimonioResponseDTO> findAllByCategoriaName(String nomeCategoria) {
        List<ItemPatrimonio> itensPatrimonio = itemPatrimonioRepository.findAllByPatrimonio_Categoria_NomeContainingIgnoreCase(nomeCategoria);
        return itensPatrimonio.stream().map(itemPatrimonioMapper::itemPatrimonioToPatrimonioResponseDto).toList();
    }

}