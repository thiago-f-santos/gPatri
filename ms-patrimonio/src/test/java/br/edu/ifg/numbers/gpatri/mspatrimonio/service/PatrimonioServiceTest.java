package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Categoria;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.TipoControle;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.CategoriaMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.PatrimonioMapper;
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
@DisplayName("Testes Unitários para PatrimonioService")
class PatrimonioServiceTest {

    @Mock
    private PatrimonioRepository patrimonioRepository;

    @Mock
    private PatrimonioMapper patrimonioMapper;

    @Mock
    private CategoriaMapper categoriaMapper;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private PatrimonioService patrimonioService;

    private UUID patrimonioId;
    private UUID categoriaId;
    private PatrimonioCreateDTO patrimonioCreateDTO;
    private PatrimonioUpdateDTO patrimonioUpdateDTO;
    private Patrimonio patrimonio;
    private Patrimonio patrimonioAtualizado;
    private PatrimonioResponseDTO patrimonioResponseDTO;
    private Categoria categoria;
    private CategoriaResponseDTO categoriaResponseDTO;

    @BeforeEach
    void setUp() {
        patrimonioId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        patrimonioCreateDTO = new PatrimonioCreateDTO();
        patrimonioCreateDTO.setNome("Patrimônio de Teste");
        patrimonioCreateDTO.setDescricao("Descrição do Patrimônio");
        patrimonioCreateDTO.setPrecoEstimado(100.0);
        patrimonioCreateDTO.setTipoControle(TipoControle.UNITARIO);
        patrimonioCreateDTO.setIdCategoria(categoriaId);

        patrimonioUpdateDTO = new PatrimonioUpdateDTO();
        patrimonioUpdateDTO.setNome("Patrimônio Atualizado");
        patrimonioUpdateDTO.setDescricao("Nova descrição");
        patrimonioUpdateDTO.setTipoControle(TipoControle.UNITARIO);

        categoria = new Categoria();
        categoria.setId(categoriaId);
        categoria.setNome("Categoria de Teste");

        patrimonio = new Patrimonio();
        patrimonio.setId(patrimonioId);
        patrimonio.setNome("Patrimônio de Teste");
        patrimonio.setCategoria(categoria);
        patrimonio.setCreatedAt(Instant.now());

        patrimonioAtualizado = new Patrimonio();
        patrimonioAtualizado.setId(patrimonioId);
        patrimonioAtualizado.setNome("Patrimônio Atualizado");
        patrimonioAtualizado.setDescricao("Nova descrição");
        patrimonioAtualizado.setTipoControle(TipoControle.UNITARIO);
        patrimonioAtualizado.setCategoria(categoria);

        categoriaResponseDTO = new CategoriaResponseDTO();
        categoriaResponseDTO.setId(categoriaId);
        categoriaResponseDTO.setNome("Categoria de Teste");
        categoriaResponseDTO.setIdCategoriaMae(null);
        categoriaResponseDTO.setCategoriaMaeNome(null);
        patrimonioResponseDTO = new PatrimonioResponseDTO();
        patrimonioResponseDTO.setId(patrimonioId);
        patrimonioResponseDTO.setNome("Patrimônio de Teste");
        patrimonioResponseDTO.setIdCategoria(categoriaId);
        patrimonioResponseDTO.setNomeCategoria("Categoria de Teste");
    }

    @Test
    @DisplayName("Deve salvar um patrimônio com sucesso")
    void salvarPatrimonio() {
        when(patrimonioMapper.createDtoToPatrimonio(patrimonioCreateDTO)).thenReturn(patrimonio);
        when(categoriaService.findById(categoriaId)).thenReturn(categoriaResponseDTO);
        when(categoriaMapper.responseDtoToCategoria(categoriaResponseDTO)).thenReturn(categoria);
        when(patrimonioRepository.save(any(Patrimonio.class))).thenReturn(patrimonio);
        when(patrimonioMapper.patrimonioToPatrimonioResponseDto(patrimonio)).thenReturn(patrimonioResponseDTO);

        PatrimonioResponseDTO response = patrimonioService.save(patrimonioCreateDTO);

        assertNotNull(response);
        assertEquals(patrimonioResponseDTO.getNome(), response.getNome());
        verify(patrimonioRepository, times(1)).save(any(Patrimonio.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se a categoria não for encontrada ao salvar")
    void salvarCategoriaNaoEncontrada() {
        when(categoriaService.findById(categoriaId)).thenThrow(new EntityNotFoundException("Categoria não encontrada"));

        assertThrows(EntityNotFoundException.class, () -> patrimonioService.save(patrimonioCreateDTO));

        verify(patrimonioRepository, never()).save(any(Patrimonio.class));
    }

    @Test
    @DisplayName("Deve atualizar o nome de um patrimônio com sucesso")
    void atualizarNomePatrimonioComSucesso() {
        PatrimonioUpdateDTO dto = new PatrimonioUpdateDTO();
        dto.setNome("Patrimônio de Teste");

        when(patrimonioRepository.findById(patrimonioId)).thenReturn(Optional.of(patrimonio));
        when(patrimonioRepository.save(any(Patrimonio.class))).thenReturn(patrimonioAtualizado);
        when(patrimonioMapper.patrimonioToPatrimonioResponseDto(patrimonioAtualizado)).thenReturn(patrimonioResponseDTO);

        PatrimonioResponseDTO resultado = patrimonioService.update(patrimonioId, dto);

        assertNotNull(resultado);
        assertEquals("Patrimônio de Teste", patrimonio.getNome());
        verify(patrimonioRepository, times(1)).save(patrimonio);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o patrimônio a ser atualizado não for encontrado")
    void atualizarPatrimonioInexistente() {
        when(patrimonioRepository.findById(patrimonioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patrimonioService.update(patrimonioId, patrimonioUpdateDTO));

        verify(patrimonioRepository, never()).save(any(Patrimonio.class));
    }

    @Test
    @DisplayName("Deve deletar um patrimônio com sucesso")
    void deletarPatrimonioComSucesso() {
        when(patrimonioRepository.existsById(patrimonioId)).thenReturn(true);

        assertDoesNotThrow(() -> patrimonioService.deleteById(patrimonioId));

        verify(patrimonioRepository, times(1)).existsById(patrimonioId);
        verify(patrimonioRepository, times(1)).deleteById(patrimonioId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o patrimônio a ser deletado não for encontrado")
    void deletarPatrimonioInexistente() {
        when(patrimonioRepository.existsById(patrimonioId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> patrimonioService.deleteById(patrimonioId));

        verify(patrimonioRepository, times(1)).existsById(patrimonioId);
        verify(patrimonioRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve encontrar um patrimônio por ID com sucesso")
    void encontrarPatrimonioPorId() {
        when(patrimonioRepository.findById(patrimonioId)).thenReturn(Optional.of(patrimonio));
        when(patrimonioMapper.patrimonioToPatrimonioResponseDto(patrimonio)).thenReturn(patrimonioResponseDTO);

        PatrimonioResponseDTO resultado = patrimonioService.findById(patrimonioId);

        assertNotNull(resultado);
        assertEquals(patrimonioResponseDTO.getId(), resultado.getId());
        verify(patrimonioRepository, times(1)).findById(patrimonioId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o patrimônio não for encontrado por ID")
    void encontrarPatrimonioInexistente() {
        when(patrimonioRepository.findById(patrimonioId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patrimonioService.findById(patrimonioId));

        verify(patrimonioRepository, times(1)).findById(patrimonioId);
    }

    @Test
    @DisplayName("Deve encontrar todos os patrimônios com sucesso")
    void encontrarTodosPatrimonios() {
        List<Patrimonio> listaDePatrimonios = Collections.singletonList(patrimonio);

        when(patrimonioRepository.findAll()).thenReturn(listaDePatrimonios);
        when(patrimonioMapper.patrimonioToPatrimonioResponseDto(any(Patrimonio.class))).thenReturn(patrimonioResponseDTO);

        List<PatrimonioResponseDTO> resultado = patrimonioService.findAll();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());

        verify(patrimonioRepository, times(1)).findAll();
        verify(patrimonioMapper, times(1)).patrimonioToPatrimonioResponseDto(any(Patrimonio.class));
    }
}