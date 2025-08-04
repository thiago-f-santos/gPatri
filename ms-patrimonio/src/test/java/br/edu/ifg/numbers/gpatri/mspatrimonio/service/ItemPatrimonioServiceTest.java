package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.CondicaoProduto;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.ItemPatrimonioMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.ItemPatrimonioRepository;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.PatrimonioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para ItemPatrimonioService")
class ItemPatrimonioServiceTest {

    @Mock
    private ItemPatrimonioRepository itemPatrimonioRepository;

    @Mock
    private ItemPatrimonioMapper itemPatrimonioMapper;

    @Mock
    private PatrimonioRepository patrimonioRepository;

    @InjectMocks
    private ItemPatrimonioService itemPatrimonioService;

    private UUID itemId;
    private UUID patrimonioId;
    private ItemPatrimonioCreateDTO itemPatrimonioCreateDTO;
    private ItemPatrimonioUpdateDTO itemPatrimonioUpdateDTO;
    private ItemPatrimonio itemPatrimonio;
    private ItemPatrimonioResponseDTO itemPatrimonioResponseDTO;
    private Patrimonio patrimonio;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        patrimonioId = UUID.randomUUID();

        itemPatrimonioCreateDTO = new ItemPatrimonioCreateDTO();
        itemPatrimonioCreateDTO.setIdPatrimonio(patrimonioId);
        itemPatrimonioCreateDTO.setCondicaoProduto(CondicaoProduto.BOM);
        itemPatrimonioCreateDTO.setCondicaoDescricao("Item em boas condições");
        itemPatrimonioCreateDTO.setQuantidade(10);

        itemPatrimonioUpdateDTO = new ItemPatrimonioUpdateDTO();
        itemPatrimonioUpdateDTO.setIdPatrimonio(patrimonioId);
        itemPatrimonioUpdateDTO.setCondicaoProduto(CondicaoProduto.EXCELENTE);
        itemPatrimonioUpdateDTO.setCondicaoDescricao("Descrição atualizada");
        itemPatrimonioUpdateDTO.setQuantidade(5);

        patrimonio = new Patrimonio();
        patrimonio.setId(patrimonioId);

        itemPatrimonio = new ItemPatrimonio();
        itemPatrimonio.setId(itemId);
        itemPatrimonio.setPatrimonio(patrimonio);
        itemPatrimonio.setCondicaoProduto(CondicaoProduto.BOM);
        itemPatrimonio.setCondicaoDescricao("Item em boas condições");
        itemPatrimonio.setQuantidade(10);
        itemPatrimonio.setCreatedAt(Instant.now());

        itemPatrimonioResponseDTO = new ItemPatrimonioResponseDTO();
        itemPatrimonioResponseDTO.setId(itemId);
        itemPatrimonioResponseDTO.setPatrimonio(new PatrimonioResponseDTO()); // Mock de PatrimonioResponseDTO
        itemPatrimonioResponseDTO.setCondicaoProduto(CondicaoProduto.BOM);
        itemPatrimonioResponseDTO.setCondicaoDescricao("Item em boas condições");
        itemPatrimonioResponseDTO.setQuantidade(10);
    }

    @Test
    @DisplayName("Deve salvar um item de patrimônio com sucesso")
    void salvarItemPatrimonio() {
        when(patrimonioRepository.findById(patrimonioId)).thenReturn(Optional.of(patrimonio));
        when(itemPatrimonioMapper.createDtoToItemPatrimonio(itemPatrimonioCreateDTO)).thenReturn(itemPatrimonio);
        when(itemPatrimonioRepository.save(any(ItemPatrimonio.class))).thenReturn(itemPatrimonio);
        when(itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio)).thenReturn(itemPatrimonioResponseDTO);

        ItemPatrimonioResponseDTO response = itemPatrimonioService.save(itemPatrimonioCreateDTO);

        assertNotNull(response);
        assertEquals(itemPatrimonioResponseDTO.getId(), response.getId());
        verify(patrimonioRepository, times(1)).findById(patrimonioId);
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o patrimônio não for encontrado ao salvar")
    void salvarComPatrimonioNaoEncontrado() {
        when(patrimonioRepository.findById(patrimonioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemPatrimonioService.save(itemPatrimonioCreateDTO));

        verify(itemPatrimonioRepository, never()).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve atualizar um item de patrimônio com sucesso")
    void atualizarItemPatrimonio() {
        when(itemPatrimonioRepository.findById(itemId)).thenReturn(Optional.of(itemPatrimonio));
        when(patrimonioRepository.findById(itemPatrimonioUpdateDTO.getIdPatrimonio())).thenReturn(Optional.of(patrimonio));
        when(itemPatrimonioRepository.save(any(ItemPatrimonio.class))).thenReturn(itemPatrimonio);
        when(itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio)).thenReturn(itemPatrimonioResponseDTO);

        ItemPatrimonioResponseDTO response = itemPatrimonioService.update(itemId, itemPatrimonioUpdateDTO);

        assertNotNull(response);
        assertEquals(itemPatrimonioResponseDTO.getId(), response.getId());
        verify(itemPatrimonioRepository, times(1)).findById(itemId);
        verify(patrimonioRepository, times(1)).findById(any(UUID.class));
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o item não for encontrado ao atualizar")
    void atualizarItemPatrimonioInexistente() {
        when(itemPatrimonioRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemPatrimonioService.update(itemId, itemPatrimonioUpdateDTO));

        verify(patrimonioRepository, never()).findById(any(UUID.class));
        verify(itemPatrimonioRepository, never()).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve deletar um item de patrimônio com sucesso")
    void deletarItemPatrimonio() {
        when(itemPatrimonioRepository.existsById(itemId)).thenReturn(true);

        assertDoesNotThrow(() -> itemPatrimonioService.deleteById(itemId));

        verify(itemPatrimonioRepository, times(1)).existsById(itemId);
        verify(itemPatrimonioRepository, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o item não for encontrado ao deletar")
    void deletarItemPatrimonioInexistente() {
        when(itemPatrimonioRepository.existsById(itemId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemPatrimonioService.deleteById(itemId));

        verify(itemPatrimonioRepository, times(1)).existsById(itemId);
        verify(itemPatrimonioRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve encontrar um item de patrimônio por ID com sucesso")
    void encontrarItemPatrimonioPorId() {
        when(itemPatrimonioRepository.findById(itemId)).thenReturn(Optional.of(itemPatrimonio));
        when(itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(itemPatrimonio)).thenReturn(itemPatrimonioResponseDTO);

        ItemPatrimonioResponseDTO response = itemPatrimonioService.findById(itemId);

        assertNotNull(response);
        assertEquals(itemPatrimonioResponseDTO.getId(), response.getId());
        verify(itemPatrimonioRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o item não for encontrado por ID")
    void itemPatrimonioNaoEncontradoPorId() {
        when(itemPatrimonioRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemPatrimonioService.findById(itemId));

        verify(itemPatrimonioRepository, times(1)).findById(itemId);
        verify(itemPatrimonioMapper, never()).itemPatrimonioToPatrimonioResponseDto(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve encontrar todos os itens de patrimônio com sucesso")
    void encontrarTodosItensPatrimonio() {
        List<ItemPatrimonio> listaDeItens = Collections.singletonList(itemPatrimonio);

        when(itemPatrimonioRepository.findAll()).thenReturn(listaDeItens);
        when(itemPatrimonioMapper.itemPatrimonioToPatrimonioResponseDto(any(ItemPatrimonio.class))).thenReturn(itemPatrimonioResponseDTO);

        List<ItemPatrimonioResponseDTO> response = itemPatrimonioService.findAll();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());

        verify(itemPatrimonioRepository, times(1)).findAll();
        verify(itemPatrimonioMapper, times(1)).itemPatrimonioToPatrimonioResponseDto(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há itens de patrimônio")
    void retornarListaVazia() {
        when(itemPatrimonioRepository.findAll()).thenReturn(Collections.emptyList());

        List<ItemPatrimonioResponseDTO> response = itemPatrimonioService.findAll();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(itemPatrimonioRepository, times(1)).findAll();
        verify(itemPatrimonioMapper, never()).itemPatrimonioToPatrimonioResponseDto(any(ItemPatrimonio.class));
    }
}