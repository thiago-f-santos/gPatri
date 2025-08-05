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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para ItemEmprestimoService")
class ItemEmprestimoServiceTest {

    @Mock
    private ItemEmprestimoRepository itemEmprestimoRepository;

    @Mock
    private ItemEmprestimoMapper itemEmprestimoMapper;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private ItemPatrimonioRepository itemPatrimonioRepository;

    @InjectMocks
    private ItemEmprestimoService itemEmprestimoService;

    private UUID idEmprestimo;
    private UUID idItemPatrimonio;
    private ItemEmprestimoCreateDTO itemEmprestimoCreateDTO;
    private ItemEmprestimoUpdateDTO itemEmprestimoUpdateDTO;
    private Emprestimo emprestimo;
    private Emprestimo emprestimoResolvido;
    private ItemPatrimonio itemPatrimonio;
    private ItemPatrimonio itemPatrimonioComEstoqueZero;
    private ItemEmprestimo itemEmprestimo;
    private ItemEmprestimoResponseDTO itemEmprestimoResponseDTO;

    @BeforeEach
    void setUp() {
        idEmprestimo = UUID.randomUUID();
        idItemPatrimonio = UUID.randomUUID();

        itemEmprestimoCreateDTO = new ItemEmprestimoCreateDTO();
        itemEmprestimoCreateDTO.setIdItemPatrimonio(idItemPatrimonio);
        itemEmprestimoCreateDTO.setQuantidade(5);

        itemEmprestimoUpdateDTO = new ItemEmprestimoUpdateDTO();
        itemEmprestimoUpdateDTO.setIdItemPatrimonio(idItemPatrimonio);
        itemEmprestimoUpdateDTO.setQuantidade(3);

        emprestimo = new Emprestimo();
        emprestimo.setId(idEmprestimo);
        emprestimo.setSituacao(SituacaoEmprestimo.APROVADO);

        emprestimoResolvido = new Emprestimo();
        emprestimoResolvido.setId(idEmprestimo);
        emprestimoResolvido.setSituacao(SituacaoEmprestimo.DEVOLVIDO);

        itemPatrimonio = new ItemPatrimonio();
        itemPatrimonio.setId(idItemPatrimonio);
        itemPatrimonio.setQuantidade(10);

        itemPatrimonioComEstoqueZero = new ItemPatrimonio();
        itemPatrimonioComEstoqueZero.setId(idItemPatrimonio);
        itemPatrimonioComEstoqueZero.setQuantidade(0);

        ItemEmprestimoId itemEmprestimoId = new ItemEmprestimoId();
        itemEmprestimoId.setIdEmprestimo(idEmprestimo);
        itemEmprestimoId.setIdItemPatrimonio(idItemPatrimonio);

        itemEmprestimo = new ItemEmprestimo();
        itemEmprestimo.setId(itemEmprestimoId);
        itemEmprestimo.setEmprestimo(emprestimo);
        itemEmprestimo.setItemPatrimonio(itemPatrimonio);
        itemEmprestimo.setQuantidade(5);

        itemEmprestimoResponseDTO = new ItemEmprestimoResponseDTO();
        itemEmprestimoResponseDTO.setItemPatrimonio(null);
        itemEmprestimoResponseDTO.setQuantidade(5);
    }

    @Test
    @DisplayName("Deve adicionar um item a um empréstimo com sucesso")
    void adcionarItem() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));
        when(itemEmprestimoMapper.createDtoToItemEmprestimo(any(ItemEmprestimoCreateDTO.class))).thenReturn(itemEmprestimo);
        when(itemEmprestimoRepository.save(any(ItemEmprestimo.class))).thenReturn(itemEmprestimo);
        when(itemPatrimonioRepository.save(any(ItemPatrimonio.class))).thenReturn(itemPatrimonio);
        when(itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(any(ItemEmprestimo.class))).thenReturn(itemEmprestimoResponseDTO);

        ItemEmprestimoResponseDTO resultado = itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO);

        assertNotNull(resultado);
        verify(emprestimoRepository, times(1)).findById(idEmprestimo);
        verify(itemPatrimonioRepository, times(1)).findById(idItemPatrimonio);
        verify(itemEmprestimoRepository, times(1)).save(any(ItemEmprestimo.class));
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o empréstimo não for encontrado")
    void emprestimoNaoEncontradoAoAdicionarItem() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO));

        verify(itemEmprestimoRepository, never()).save(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar SituacaoEmprestimoInvalidaException se o empréstimo já foi resolvido")
    void emprestimoResolvidoAoAdicionarItem() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimoResolvido));

        assertThrows(SituacaoEmprestimoInvalidaException.class, () -> itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO));

        verify(itemPatrimonioRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o ItemPatrimonio não for encontrado")
    void itemPatrimonioNaoEncontradoAoAdicionarItem() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO));

        verify(itemEmprestimoRepository, never()).save(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar ItemEmUsoException se o ItemPatrimonio estiver com estoque zero")
    void itemPatrimonioComEstoqueZeroAoAdicionarItem() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonioComEstoqueZero));

        assertThrows(ItemEmUsoException.class, () -> itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO));

        verify(itemEmprestimoRepository, never()).save(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar QuantidadeItemIndisponivelException se a quantidade for insuficiente")
    void quantidadeInsuficiente() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        itemPatrimonio.setQuantidade(2); // Estoque de 2, mas 5 estão sendo solicitados
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));

        assertThrows(QuantidadeItemIndisponivelException.class, () -> itemEmprestimoService.adicionaItemEmprestimo(idEmprestimo, itemEmprestimoCreateDTO));

        verify(itemEmprestimoRepository, never()).save(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve atualizar um item de empréstimo com sucesso")
    void atualizarItemEmprestimo() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));
        when(itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(any(UUID.class), any(UUID.class))).thenReturn(itemEmprestimo);
        when(itemEmprestimoRepository.save(any(ItemEmprestimo.class))).thenReturn(itemEmprestimo);
        when(itemPatrimonioRepository.save(any(ItemPatrimonio.class))).thenReturn(itemPatrimonio);
        when(itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(any(ItemEmprestimo.class))).thenReturn(itemEmprestimoResponseDTO);

        ItemEmprestimoResponseDTO resultado = itemEmprestimoService.atualizaItemEmprestimo(idEmprestimo, itemEmprestimoUpdateDTO);

        assertNotNull(resultado);
        assertEquals(itemEmprestimoResponseDTO.getQuantidade(), resultado.getQuantidade());
        verify(emprestimoRepository, times(1)).findById(idEmprestimo);
        verify(itemPatrimonioRepository, times(1)).findById(idItemPatrimonio);
        verify(itemEmprestimoRepository, times(1)).save(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar SituacaoEmprestimoInvalidaException se o empréstimo já foi resolvido")
    void emprestimoResolvidoAoAtualizarItemEmprestimo() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimoResolvido));

        assertThrows(SituacaoEmprestimoInvalidaException.class, () -> itemEmprestimoService.atualizaItemEmprestimo(idEmprestimo, itemEmprestimoUpdateDTO));

        verify(itemPatrimonioRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o ItemEmprestimo não for encontrado ao atualizar")
    void itemEmprestimoNaoEncontrado() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));
        when(itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(any(UUID.class), any(UUID.class))).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> itemEmprestimoService.atualizaItemEmprestimo(idEmprestimo, itemEmprestimoUpdateDTO));
    }

    @Test
    @DisplayName("Deve apagar um item de empréstimo com sucesso")
    void apagarItemEmprestimo() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio)).thenReturn(itemEmprestimo);
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));

        assertDoesNotThrow(() -> itemEmprestimoService.apagarItemEmprestimo(idEmprestimo, idItemPatrimonio));

        verify(emprestimoRepository, times(1)).findById(idEmprestimo);
        verify(itemEmprestimoRepository, times(1)).findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio);
        verify(itemPatrimonioRepository, times(1)).findById(idItemPatrimonio);
        verify(itemEmprestimoRepository, times(1)).delete(itemEmprestimo);
        verify(itemPatrimonioRepository, times(1)).save(itemPatrimonio);
    }

    @Test
    @DisplayName("Deve lançar SituacaoEmprestimoInvalidaException se o empréstimo já foi resolvido ao apagar item")
    void situacaoEmprestimoInvalidaAoApagarItemEmprestimo() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimoResolvido));

        assertThrows(SituacaoEmprestimoInvalidaException.class, () -> itemEmprestimoService.apagarItemEmprestimo(idEmprestimo, idItemPatrimonio));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o item do empréstimo não for encontrado ao apagar")
    void itemEmprestimoNaoEncontradoAoApagar() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> itemEmprestimoService.apagarItemEmprestimo(idEmprestimo, idItemPatrimonio));
    }

    @Test
    @DisplayName("Deve encontrar um item de empréstimo por ID com sucesso")
    void EncontrarItemEmprestimoPorId() {
        when(itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio)).thenReturn(itemEmprestimo);
        when(itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(itemEmprestimo)).thenReturn(itemEmprestimoResponseDTO);

        ItemEmprestimoResponseDTO resultado = itemEmprestimoService.findById(idEmprestimo, idItemPatrimonio);

        assertNotNull(resultado);
        verify(itemEmprestimoRepository, times(1)).findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio);
        verify(itemEmprestimoMapper, times(1)).itemEmprestimoToItemEmprestimoResponseDTO(itemEmprestimo);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o item não for encontrado por ID")
    void itemNaoEncontradoPorId() {
        when(itemEmprestimoRepository.findByEmprestimo_IdEqualsAndItemPatrimonio_IdEquals(idEmprestimo, idItemPatrimonio)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> itemEmprestimoService.findById(idEmprestimo, idItemPatrimonio));
    }

    @Test
    @DisplayName("Deve encontrar todos os itens de empréstimo para um dado empréstimo com sucesso")
    void encontrarTodosItensDeEmprestimo() {
        when(itemEmprestimoRepository.findAllByEmprestimo_IdEquals(idEmprestimo)).thenReturn(Collections.singletonList(itemEmprestimo));
        when(itemEmprestimoMapper.itemEmprestimoToItemEmprestimoResponseDTO(any(ItemEmprestimo.class))).thenReturn(itemEmprestimoResponseDTO);

        List<ItemEmprestimoResponseDTO> resultado = itemEmprestimoService.findAllByEmprestimo(idEmprestimo);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        verify(itemEmprestimoRepository, times(1)).findAllByEmprestimo_IdEquals(idEmprestimo);
        verify(itemEmprestimoMapper, times(1)).itemEmprestimoToItemEmprestimoResponseDTO(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há itens de empréstimo")
    void retornarListaVazia() {
        when(itemEmprestimoRepository.findAllByEmprestimo_IdEquals(idEmprestimo)).thenReturn(Collections.emptyList());

        List<ItemEmprestimoResponseDTO> resultado = itemEmprestimoService.findAllByEmprestimo(idEmprestimo);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}