package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.service.PermissaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração e Segurança para PermissaoController")
class PermissaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissaoService permissaoService;

    private PermissaoResponseDTO permissao1;
    private PermissaoResponseDTO permissao2;
    private UUID permissaoId1;
    private UUID permissaoId2;

    @BeforeEach
    void setUp() {
        permissaoId1 = UUID.randomUUID();
        permissaoId2 = UUID.randomUUID();

        permissao1 = PermissaoResponseDTO.builder()
                .id(permissaoId1)
                .nome("PERMISSAO_LISTAR")
                .descricao("Permite listar permissões")
                .categoria("PERMISSOES")
                .build();

        permissao2 = PermissaoResponseDTO.builder()
                .id(permissaoId2)
                .nome("CARGO_LISTAR")
                .descricao("Permite listar cargos")
                .categoria("CARGOS")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/permissoes - Deve retornar 200 OK com lista de permissões quando usuário tem autoridade PERMISSAO_LISTAR")
    void deveRetornarTodasPermissoesComAutoridade() throws Exception {
        when(permissaoService.buscarTodas()).thenReturn(List.of(permissao1, permissao2));

        mockMvc.perform(get("/api/v1/permissoes")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("PERMISSAO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(permissaoId1.toString()))
                .andExpect(jsonPath("$[0].nome").value("PERMISSAO_LISTAR"))
                .andExpect(jsonPath("$[0].categoria").value("PERMISSOES"))
                .andExpect(jsonPath("$[1].id").value(permissaoId2.toString()))
                .andExpect(jsonPath("$[1].nome").value("CARGO_LISTAR"))
                .andExpect(jsonPath("$[1].categoria").value("CARGOS"));

        verify(permissaoService, times(1)).buscarTodas();
    }

    @Test
    @DisplayName("GET /api/v1/permissoes?categoria=CARGOS - Deve retornar 200 OK filtrando por categoria quando usuário tem autoridade PERMISSAO_LISTAR")
    void deveRetornarPermissoesPorCategoriaComAutoridade() throws Exception {
        when(permissaoService.buscarPorCategoria("CARGOS")).thenReturn(List.of(permissao2));

        mockMvc.perform(get("/api/v1/permissoes")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("PERMISSAO_LISTAR")))
                        .param("categoria", "CARGOS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(permissaoId2.toString()))
                .andExpect(jsonPath("$[0].nome").value("CARGO_LISTAR"))
                .andExpect(jsonPath("$[0].categoria").value("CARGOS"));

        verify(permissaoService, times(1)).buscarPorCategoria("CARGOS");
    }

    @Test
    @DisplayName("GET /api/v1/permissoes - Deve retornar 403 Forbidden quando usuário não possui autoridade PERMISSAO_LISTAR")
    void deveRetornarForbiddenAoBuscarTodasSemAutoridade() throws Exception {
        mockMvc.perform(get("/api/v1/permissoes")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("USUARIO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Acesso negado. Você não tem permissão para acessar este recurso."));

        verify(permissaoService, never()).buscarTodas();
    }

    @Test
    @DisplayName("GET /api/v1/permissoes - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoBuscarTodasSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/permissoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuario não autenticado, por favor faça login."));

        verify(permissaoService, never()).buscarTodas();
    }

    @Test
    @DisplayName("GET /api/v1/permissoes/{id} - Deve retornar 200 OK quando ID existe e usuário possui autoridade PERMISSAO_LISTAR")
    void deveRetornarPermissaoPorIdComAutoridade() throws Exception {
        when(permissaoService.findById(permissaoId1)).thenReturn(permissao1);

        mockMvc.perform(get("/api/v1/permissoes/{id}", permissaoId1)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("PERMISSAO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(permissaoId1.toString()))
                .andExpect(jsonPath("$.nome").value("PERMISSAO_LISTAR"))
                .andExpect(jsonPath("$.descricao").value("Permite listar permissões"))
                .andExpect(jsonPath("$.categoria").value("PERMISSOES"));

        verify(permissaoService, times(1)).findById(permissaoId1);
    }

    @Test
    @DisplayName("GET /api/v1/permissoes/{id} - Deve retornar 404 Not Found quando permissão não existe")
    void deveRetornarNotFoundAoBuscarPermissaoPorIdInexistente() throws Exception {
        when(permissaoService.findById(permissaoId1)).thenThrow(new ResourceNotFoundException("Permissão não encontrada"));

        mockMvc.perform(get("/api/v1/permissoes/{id}", permissaoId1)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("PERMISSAO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Permissão não encontrada"));

        verify(permissaoService, times(1)).findById(permissaoId1);
    }

    @Test
    @DisplayName("GET /api/v1/permissoes/{id} - Deve retornar 403 Forbidden quando usuário não possui autoridade PERMISSAO_LISTAR")
    void deveRetornarForbiddenAoBuscarPorIdSemAutoridade() throws Exception {
        mockMvc.perform(get("/api/v1/permissoes/{id}", permissaoId1)
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("USUARIO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Acesso negado. Você não tem permissão para acessar este recurso."));

        verify(permissaoService, never()).findById(any());
    }

    @Test
    @DisplayName("GET /api/v1/permissoes/{id} - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoBuscarPorIdSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/permissoes/{id}", permissaoId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuario não autenticado, por favor faça login."));

        verify(permissaoService, never()).findById(any());
    }
}
