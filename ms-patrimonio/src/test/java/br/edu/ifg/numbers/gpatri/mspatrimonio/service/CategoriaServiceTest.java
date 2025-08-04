package br.edu.ifg.numbers.gpatri.mspatrimonio.service;


import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Categoria;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.CategoriaMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CategoriaService")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    private UUID categoriaId;
    private UUID categoriaMaeId;
    private UUID novaCategoriaMaeId;
    private CategoriaCreateDTO categoriaCreateDTO;
    private CategoriaCreateDTO categoriaRaizCreateDTO;
    private Categoria categoria;
    private Categoria categoriaRaiz;
    private Categoria categoriaMae;
    private Categoria novaCategoriaMae;
    private CategoriaResponseDTO categoriaResponseDTO;
    private CategoriaResponseDTO categoriaRaizResponseDTO;
    private CategoriaUpdateDTO categoriaUpdateDTO;

    @BeforeEach
    void setUp() {

        categoriaId = UUID.randomUUID();
        categoriaMaeId = UUID.randomUUID();
        novaCategoriaMaeId = UUID.randomUUID();

        categoriaCreateDTO = new CategoriaCreateDTO();
        categoriaCreateDTO.setNome("Nova Categoria");
        categoriaCreateDTO.setIdCategoriaMae(categoriaMaeId);

        categoriaRaizCreateDTO = new CategoriaCreateDTO();
        categoriaRaizCreateDTO.setNome("Categoria Raiz");
        categoriaRaizCreateDTO.setIdCategoriaMae(null);

        categoriaUpdateDTO = new CategoriaUpdateDTO();
        categoriaUpdateDTO.setNome("Nome atualizado");
        categoriaUpdateDTO.setIdCategoriaMae(novaCategoriaMaeId);

        categoriaMae = new Categoria();
        categoriaMae.setId(categoriaMaeId);
        categoriaMae.setNome("Categoria Mae");
        categoriaMae.setSubcategorias(new HashSet<>());

        novaCategoriaMae = new Categoria();
        novaCategoriaMae.setId(novaCategoriaMaeId);
        novaCategoriaMae.setNome("Nova Categoria Mae");
        novaCategoriaMae.setSubcategorias(new HashSet<>());

        categoria = new Categoria();
        categoria.setId(categoriaId);
        categoria.setNome("Nova Categoria");
        categoria.setCategoriaMae(categoriaMae);
        categoria.setCreatedAt(Instant.now());

        categoriaRaiz = new Categoria();
        categoriaRaiz.setId(UUID.randomUUID());
        categoriaRaiz.setNome("Categoria Raiz");
        categoriaRaiz.setCategoriaMae(null);

        categoriaResponseDTO = new CategoriaResponseDTO();
        categoriaResponseDTO.setId(categoriaId);
        categoriaResponseDTO.setNome("Nova Categoria");
        categoriaResponseDTO.setIdCategoriaMae(categoriaMaeId);
        categoriaResponseDTO.setCategoriaMaeNome("Categoria Mae");

        categoriaRaizResponseDTO = new CategoriaResponseDTO();
        categoriaRaizResponseDTO.setId(categoriaRaiz.getId());
        categoriaRaizResponseDTO.setNome("Categoria Raiz");
        categoriaRaizResponseDTO.setIdCategoriaMae(null);
        categoriaRaizResponseDTO.setCategoriaMaeNome(null);

    }

    @Test
    @DisplayName("Deve salvar uma subcategoria com sucesso")
    void salvarSubcategoria() {

        when(categoriaRepository.findById(categoriaMaeId)).thenReturn(Optional.of(categoriaMae));
        when(categoriaMapper.createDtoToCategoria(categoriaCreateDTO)).thenReturn(categoria);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoria)).thenReturn(categoriaResponseDTO);

        CategoriaResponseDTO response = categoriaService.save(categoriaCreateDTO);

        assertNotNull(response);
        assertEquals(categoriaResponseDTO.getId(), response.getId());
        assertEquals(categoriaResponseDTO.getNome(), response.getNome());
        assertEquals(categoriaMaeId, response.getIdCategoriaMae());

        verify(categoriaRepository, times(1)).findById(categoriaMaeId);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve salvar uma categoria raiz com sucesso")
    void salvarCategoriaRaiz() {

        when(categoriaMapper.createDtoToCategoria(categoriaRaizCreateDTO)).thenReturn(categoriaRaiz);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaRaiz);
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoriaRaiz)).thenReturn(categoriaRaizResponseDTO);

        CategoriaResponseDTO response = categoriaService.save(categoriaRaizCreateDTO);

        assertNotNull(response);
        assertEquals(categoriaRaizResponseDTO.getId(), response.getId());
        assertEquals(categoriaRaizResponseDTO.getNome(), response.getNome());
        assertNull(response.getIdCategoriaMae());

        verify(categoriaRepository, never()).findById(any(UUID.class));
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se a categria-mãe não for encontrada")
    void categoriaMaeNaoEncontrada() {

        when(categoriaRepository.findById(categoriaMaeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoriaService.save(categoriaCreateDTO));

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve atualizar uma categoria com sucesso")
    void atualizarCategoria() {

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findById(novaCategoriaMaeId)).thenReturn(Optional.of(novaCategoriaMae));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoria)).thenReturn(categoriaResponseDTO);

        CategoriaResponseDTO response = categoriaService.update(categoriaId, categoriaUpdateDTO);

        assertNotNull(response);
        assertEquals(categoriaUpdateDTO.getNome(), categoria.getNome());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve remover a categoria da antiga mãe e adcionar a nova mãe ao atualizar categoria")
    void mudarCategoriaMae() {

        categoria.setCategoriaMae(categoriaMae);
        categoriaMae.getSubcategorias().add(categoria);

        CategoriaUpdateDTO categoriaUpdateDTO = new CategoriaUpdateDTO();
        categoriaUpdateDTO.setIdCategoriaMae(novaCategoriaMaeId);

        CategoriaResponseDTO dtoResposta = new CategoriaResponseDTO();
        dtoResposta.setId(categoriaId);
        dtoResposta.setNome(categoria.getNome());
        dtoResposta.setIdCategoriaMae(novaCategoriaMaeId);
        dtoResposta.setCategoriaMaeNome(novaCategoriaMae.getNome());

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findById(novaCategoriaMaeId)).thenReturn(Optional.of(novaCategoriaMae));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoria)).thenReturn(dtoResposta);

        assertTrue(categoriaMae.getSubcategorias().contains(categoria));
        categoriaService.update(categoriaId, categoriaUpdateDTO);

        verify(categoriaRepository, times(1)).save(any(Categoria.class));
        assertFalse(categoriaMae.getSubcategorias().contains(categoria));
        assertFalse(categoriaMae.getSubcategorias().contains(categoria));
    }

    @Test
    @DisplayName("Deve remover a categoria mãe e tornar categoria raiz")
    void removerCategoriaMae() {

        categoria.setCategoriaMae(categoriaMae);
        categoriaMae.getSubcategorias().add(categoria);

        CategoriaUpdateDTO categoriaUpdateDTO = new CategoriaUpdateDTO();
        categoriaUpdateDTO.setIdCategoriaMae(null);

        CategoriaResponseDTO dtoRespostaAtualizada = new CategoriaResponseDTO();
        dtoRespostaAtualizada.setId(categoriaId);
        dtoRespostaAtualizada.setNome(categoria.getNome());
        dtoRespostaAtualizada.setIdCategoriaMae(null);
        dtoRespostaAtualizada.setCategoriaMaeNome(null);

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoria)).thenReturn(dtoRespostaAtualizada);

        assertTrue(categoriaMae.getSubcategorias().contains(categoria));
        categoriaService.update(categoriaId, categoriaUpdateDTO);

        verify(categoriaRepository, times(1)).save(any(Categoria.class));
        assertFalse(categoriaMae.getSubcategorias().contains(categoria));
        assertNull(categoria.getCategoriaMae());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao atualizar com uma categoria mae inexistente")
    void atualizarCategoriaMaeNaoEncontrada() {

        CategoriaUpdateDTO categoriaUpdateDTO = new CategoriaUpdateDTO();
        categoriaUpdateDTO.setIdCategoriaMae(UUID.randomUUID());

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.findById(categoriaUpdateDTO.getIdCategoriaMae())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoriaService.update(categoriaId, categoriaUpdateDTO));

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao atualizar categoria inexistente")
    void atualizarCategoriaNaoEncontrada() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoriaService.update(categoriaId, categoriaUpdateDTO));
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void deletarCategoria() {

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).delete(categoria);

        categoriaService.deleteById(categoriaId);

        verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao deletar categoria inexistente")
    void deletarCategoriaNaoEncontrada() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoriaService.deleteById(categoriaId));
    }

    @Test
    @DisplayName("Deve retornar categoria por id com sucesso")
    void buscarCategoriaPorId() {

        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoria)).thenReturn(categoriaResponseDTO);

        CategoriaResponseDTO response = categoriaService.findById(categoriaId);

        assertNotNull(response);
        assertEquals(categoriaId, response.getId());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar categoria inexistente")
    void buscarCategoriaPorIdNaoEncontrada() {
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoriaService.findById(categoriaId));
    }

    @Test
    @DisplayName("Deve retornar lista de categorias")
    void buscarTodasCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria, categoriaRaiz));
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoria)).thenReturn(categoriaResponseDTO);
        when(categoriaMapper.categoriaToCategoriaResponseDTO(categoriaRaiz)).thenReturn(categoriaRaizResponseDTO);

        var lista = categoriaService.findAll();

        assertEquals(2, lista.size());
        assertTrue(lista.stream().anyMatch(c -> c.getId().equals(categoriaId)));
    }
}
