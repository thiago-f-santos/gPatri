package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.PermissaoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o PermissaoService")
class PermissaoServiceTest {

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private PermissaoMapper permissaoMapper;

    @InjectMocks
    private PermissaoService permissaoService;

    private Permissao permissao1;
    private Permissao permissao2;
    private PermissaoResponseDTO permissaoResponseDTO1;
    private PermissaoResponseDTO permissaoResponseDTO2;
    private UUID permissaoId1;
    private UUID permissaoId2;

    @BeforeEach
    void setUp() {
        permissaoId1 = UUID.randomUUID();
        permissaoId2 = UUID.randomUUID();

        permissao1 = Permissao.builder()
                .id(permissaoId1)
                .nome("CARGO_CADASTRAR")
                .descricao("Permite cadastrar cargos")
                .categoria("CARGO")
                .build();

        permissao2 = Permissao.builder()
                .id(permissaoId2)
                .nome("CARGO_LISTAR")
                .descricao("Permite listar cargos")
                .categoria("CARGO")
                .build();

        permissaoResponseDTO1 = PermissaoResponseDTO.builder()
                .id(permissaoId1)
                .nome("CARGO_CADASTRAR")
                .descricao("Permite cadastrar cargos")
                .categoria("CARGO")
                .build();

        permissaoResponseDTO2 = PermissaoResponseDTO.builder()
                .id(permissaoId2)
                .nome("CARGO_LISTAR")
                .descricao("Permite listar cargos")
                .categoria("CARGO")
                .build();
    }

    @Test
    @DisplayName("Deve retornar todas as permissões com sucesso")
    void buscarTodas() {
        List<Permissao> permissoes = List.of(permissao1, permissao2);
        List<PermissaoResponseDTO> dtos = List.of(permissaoResponseDTO1, permissaoResponseDTO2);

        when(permissaoRepository.findAllByOrderByCategoriaAscNomeAsc()).thenReturn(permissoes);
        when(permissaoMapper.toDtoList(permissoes)).thenReturn(dtos);

        List<PermissaoResponseDTO> resultado = permissaoService.buscarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("CARGO_CADASTRAR", resultado.get(0).getNome());
        assertEquals("CARGO_LISTAR", resultado.get(1).getNome());

        verify(permissaoRepository, times(1)).findAllByOrderByCategoriaAscNomeAsc();
        verify(permissaoMapper, times(1)).toDtoList(permissoes);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver permissões")
    void buscarTodasVazio() {
        when(permissaoRepository.findAllByOrderByCategoriaAscNomeAsc()).thenReturn(Collections.emptyList());
        when(permissaoMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<PermissaoResponseDTO> resultado = permissaoService.buscarTodas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(permissaoRepository, times(1)).findAllByOrderByCategoriaAscNomeAsc();
        verify(permissaoMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    @DisplayName("Deve buscar permissão por ID com sucesso")
    void buscarPorId() {
        when(permissaoRepository.findById(permissaoId1)).thenReturn(Optional.of(permissao1));
        when(permissaoMapper.toDto(permissao1)).thenReturn(permissaoResponseDTO1);

        PermissaoResponseDTO resultado = permissaoService.findById(permissaoId1);

        assertNotNull(resultado);
        assertEquals(permissaoId1, resultado.getId());
        assertEquals("CARGO_CADASTRAR", resultado.getNome());

        verify(permissaoRepository, times(1)).findById(permissaoId1);
        verify(permissaoMapper, times(1)).toDto(permissao1);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar permissão por ID inexistente")
    void buscarPorIdInexistente() {
        when(permissaoRepository.findById(permissaoId1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissaoService.findById(permissaoId1));

        verify(permissaoRepository, times(1)).findById(permissaoId1);
        verify(permissaoMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Deve buscar permissões por categoria com sucesso")
    void buscarPorCategoria() {
        List<Permissao> permissoes = List.of(permissao1, permissao2);
        List<PermissaoResponseDTO> dtos = List.of(permissaoResponseDTO1, permissaoResponseDTO2);

        when(permissaoRepository.findByCategoriaOrderByNomeAsc("CARGO")).thenReturn(permissoes);
        when(permissaoMapper.toDtoList(permissoes)).thenReturn(dtos);

        List<PermissaoResponseDTO> resultado = permissaoService.buscarPorCategoria("CARGO");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("CARGO", resultado.get(0).getCategoria());

        verify(permissaoRepository, times(1)).findByCategoriaOrderByNomeAsc("CARGO");
        verify(permissaoMapper, times(1)).toDtoList(permissoes);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver permissões na categoria informada")
    void buscarPorCategoriaVazia() {
        when(permissaoRepository.findByCategoriaOrderByNomeAsc("INEXISTENTE")).thenReturn(Collections.emptyList());
        when(permissaoMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<PermissaoResponseDTO> resultado = permissaoService.buscarPorCategoria("INEXISTENTE");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(permissaoRepository, times(1)).findByCategoriaOrderByNomeAsc("INEXISTENTE");
        verify(permissaoMapper, times(1)).toDtoList(Collections.emptyList());
    }
}
