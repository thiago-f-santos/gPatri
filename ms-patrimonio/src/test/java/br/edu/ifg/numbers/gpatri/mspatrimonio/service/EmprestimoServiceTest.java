package br.edu.ifg.numbers.gpatri.mspatrimonio.service;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Emprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.*;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.ItemEmUsoException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.QuantidadeItemIndisponivelException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.SituacaoEmprestimoInvalidaException;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.EmprestimoMapper;
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
@DisplayName("Testes Unitários para EmprestimoService")
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;
    @Mock
    private EmprestimoMapper emprestimoMapper;
    @Mock
    private ItemEmprestimoRepository itemEmprestimoRepository;
    @Mock
    private ItemEmprestimoMapper itemEmprestimoMapper;
    @Mock
    private ItemPatrimonioRepository itemPatrimonioRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private UUID idUsuario;
    private UUID idEmprestimo;
    private UUID idItemPatrimonio;
    private EmprestimoCreateDTO emprestimoCreateDTO;
    private EmprestimoUpdateDTO emprestimoUpdateDTO;
    private Emprestimo emprestimo;
    private Emprestimo emprestimoAprovado;
    private Emprestimo emprestimoDevolvido;
    private EmprestimoResponseDTO emprestimoResponseDTO;
    private ItemPatrimonio itemPatrimonio;
    private ItemEmprestimoCreateDTO itemEmprestimoCreateDTO;
    private ItemEmprestimo itemEmprestimo;
    private List<ItemEmprestimo> itensEmprestimoList;

    @BeforeEach
    void setUp() {
        idUsuario = UUID.randomUUID();
        idEmprestimo = UUID.randomUUID();
        idItemPatrimonio = UUID.randomUUID();

        itemEmprestimoCreateDTO = new ItemEmprestimoCreateDTO();
        itemEmprestimoCreateDTO.setIdItemPatrimonio(idItemPatrimonio);
        itemEmprestimoCreateDTO.setQuantidade(1);

        emprestimoCreateDTO = new EmprestimoCreateDTO();
        emprestimoCreateDTO.setItensEmprestimo(Collections.singletonList(itemEmprestimoCreateDTO));
        emprestimoCreateDTO.setDataEmprestimo(new Date());
        emprestimoCreateDTO.setDataDevolucao(new Date());

        emprestimoUpdateDTO = new EmprestimoUpdateDTO();
        emprestimoUpdateDTO.setDataEmprestimo(new Date());

        emprestimo = new Emprestimo();
        emprestimo.setId(idEmprestimo);
        emprestimo.setIdUsuario(idUsuario);
        emprestimo.setSituacao(SituacaoEmprestimo.EM_ESPERA);
        emprestimo.setItensEmprestimo(new ArrayList<>());

        emprestimoAprovado = new Emprestimo();
        emprestimoAprovado.setId(idEmprestimo);
        emprestimoAprovado.setIdUsuario(idUsuario);
        emprestimoAprovado.setSituacao(SituacaoEmprestimo.APROVADO);
        emprestimoAprovado.setItensEmprestimo(new ArrayList<>());

        Emprestimo emprestimoNegado = new Emprestimo();
        emprestimoNegado.setId(idEmprestimo);
        emprestimoNegado.setIdUsuario(idUsuario);
        emprestimoNegado.setSituacao(SituacaoEmprestimo.NEGADO);
        emprestimoNegado.setItensEmprestimo(new ArrayList<>());

        emprestimoDevolvido = new Emprestimo();
        emprestimoDevolvido.setId(idEmprestimo);
        emprestimoDevolvido.setIdUsuario(idUsuario);
        emprestimoDevolvido.setSituacao(SituacaoEmprestimo.DEVOLVIDO);
        emprestimoDevolvido.setItensEmprestimo(new ArrayList<>());

        emprestimoResponseDTO = new EmprestimoResponseDTO();
        emprestimoResponseDTO.setId(idEmprestimo);
        emprestimoResponseDTO.setIdUsuario(idUsuario);
        emprestimoResponseDTO.setSituacao(SituacaoEmprestimo.EM_ESPERA);

        itemPatrimonio = new ItemPatrimonio();
        itemPatrimonio.setId(idItemPatrimonio);
        itemPatrimonio.setQuantidade(10);

        itemEmprestimo = new ItemEmprestimo();
        itemEmprestimo.setQuantidade(1);
        itemEmprestimo.setItemPatrimonio(itemPatrimonio);
        itemEmprestimo.setEmprestimo(emprestimo);

        itensEmprestimoList = Collections.singletonList(itemEmprestimo);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo com sucesso")
    void salvarEmprestimo() {
        when(emprestimoMapper.createDtoToEmprestimo(any(EmprestimoCreateDTO.class))).thenReturn(emprestimo);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));
        when(itemEmprestimoMapper.createDtoToItemEmprestimo(any(ItemEmprestimoCreateDTO.class))).thenReturn(itemEmprestimo);
        when(emprestimoMapper.emprestimoToEmprestimoResponseDto(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        EmprestimoResponseDTO response = emprestimoService.save(idUsuario, emprestimoCreateDTO);

        assertNotNull(response);
        assertEquals(SituacaoEmprestimo.EM_ESPERA, emprestimo.getSituacao());
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
        verify(itemEmprestimoRepository, times(1)).save(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar EmprestimoVazioException se o empréstimo não tiver itens")
    void emprestimoVazioException() {
        EmprestimoCreateDTO dtoSemItens = new EmprestimoCreateDTO();
        dtoSemItens.setItensEmprestimo(null);

        when(emprestimoMapper.createDtoToEmprestimo(any(EmprestimoCreateDTO.class))).thenReturn(null);

        assertThrows(NullPointerException.class, () -> emprestimoService.save(idUsuario, dtoSemItens));

        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar ItemEmUsoException se a quantidade do item for 0")
    void itemEmUso() {
        when(emprestimoMapper.createDtoToEmprestimo(any(EmprestimoCreateDTO.class))).thenReturn(emprestimo);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        itemPatrimonio.setQuantidade(0);
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));

        assertThrows(ItemEmUsoException.class, () -> emprestimoService.save(idUsuario, emprestimoCreateDTO));

        verify(itemPatrimonioRepository, never()).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve lançar QuantidadeItemIndisponivelException se a quantidade solicitada for maior que o estoque")
    void quantidadeItemIndisponivel() {
        when(emprestimoMapper.createDtoToEmprestimo(any(EmprestimoCreateDTO.class))).thenReturn(emprestimo);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        itemPatrimonio.setQuantidade(1);
        itemEmprestimoCreateDTO.setQuantidade(5);
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));

        assertThrows(QuantidadeItemIndisponivelException.class, () -> emprestimoService.save(idUsuario, emprestimoCreateDTO));

        verify(itemPatrimonioRepository, never()).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo com sucesso")
    void AtualizarEmprestimo() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(emprestimoMapper.emprestimoToEmprestimoResponseDto(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        EmprestimoResponseDTO resultado = emprestimoService.update(idEmprestimo, emprestimoUpdateDTO);

        assertNotNull(resultado);
        verify(emprestimoRepository, times(1)).findById(idEmprestimo);
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o empréstimo não for encontrado ao atualizar")
    void emprestimoNaoEncontradoAoAtualizar() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> emprestimoService.update(idEmprestimo, emprestimoUpdateDTO));

        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve deletar um empréstimo com sucesso")
    void deletarEmprestimo() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemEmprestimoRepository.findAllByEmprestimo_IdEquals(idEmprestimo)).thenReturn(itensEmprestimoList);
        when(itemPatrimonioRepository.findById(idItemPatrimonio)).thenReturn(Optional.of(itemPatrimonio));

        assertDoesNotThrow(() -> emprestimoService.delete(idEmprestimo));

        verify(emprestimoRepository, times(1)).findById(idEmprestimo);
        verify(emprestimoRepository, times(1)).delete(emprestimo);
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
        verify(itemEmprestimoRepository, times(1)).delete(any(ItemEmprestimo.class));
    }

    @Test
    @DisplayName("Não deve devolver itens se o empréstimo já foi devolvido ou negado")
    void naoDevolverItens() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimoDevolvido));

        assertDoesNotThrow(() -> emprestimoService.delete(idEmprestimo));

        verify(emprestimoRepository, times(1)).findById(idEmprestimo);
        verify(emprestimoRepository, times(1)).delete(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar deletar empréstimo inexistente")
    void deletarEmprestimoInexistente() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> emprestimoService.delete(idEmprestimo));

        verify(emprestimoRepository, never()).delete(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve aprovar um empréstimo com sucesso se estiver em espera")
    void aprovarEmprestimoComSucesso() {
        UUID idUsuarioAvaliador = UUID.randomUUID();
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(emprestimoMapper.emprestimoToEmprestimoResponseDto(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        EmprestimoResponseDTO resultado = emprestimoService.aprovarEmprestimo(idUsuarioAvaliador, idEmprestimo);

        assertNotNull(resultado);
        assertEquals(SituacaoEmprestimo.APROVADO, emprestimo.getSituacao());
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar SituacaoEmprestimoInvalidaException se a situação não for EM_ESPERA")
    void aprovarEmprestimoComSituacaoInvalidaException() {
        UUID idUsuarioAvaliador = UUID.randomUUID();
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimoAprovado));

        assertThrows(SituacaoEmprestimoInvalidaException.class, () -> emprestimoService.aprovarEmprestimo(idUsuarioAvaliador, idEmprestimo));

        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve negar um empréstimo com sucesso se estiver em espera")
    void negarEmprestimoComSucesso() {
        UUID idUsuarioAvaliador = UUID.randomUUID();
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimo));
        when(itemEmprestimoRepository.findAllByEmprestimo_IdEquals(any(UUID.class))).thenReturn(itensEmprestimoList);
        when(itemPatrimonioRepository.findById(any(UUID.class))).thenReturn(Optional.of(itemPatrimonio));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(emprestimoMapper.emprestimoToEmprestimoResponseDto(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        EmprestimoResponseDTO resultado = emprestimoService.negarEmprestimo(idUsuarioAvaliador, idEmprestimo);

        assertNotNull(resultado);
        assertEquals(SituacaoEmprestimo.NEGADO, emprestimo.getSituacao());
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
    }

    @Test
    @DisplayName("Deve devolver um empréstimo com sucesso se estiver aprovado")
    void devolverEmprestimoComSucesso() {
        when(emprestimoRepository.findById(idEmprestimo)).thenReturn(Optional.of(emprestimoAprovado));
        when(itemEmprestimoRepository.findAllByEmprestimo_IdEquals(any(UUID.class))).thenReturn(itensEmprestimoList);
        when(itemPatrimonioRepository.findById(any(UUID.class))).thenReturn(Optional.of(itemPatrimonio));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoAprovado);
        when(emprestimoMapper.emprestimoToEmprestimoResponseDto(any(Emprestimo.class))).thenReturn(emprestimoResponseDTO);

        EmprestimoResponseDTO resultado = emprestimoService.devolverEmprestimo(idEmprestimo);

        assertNotNull(resultado);
        assertEquals(SituacaoEmprestimo.DEVOLVIDO, emprestimoAprovado.getSituacao());
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
        verify(itemPatrimonioRepository, times(1)).save(any(ItemPatrimonio.class));
    }
}