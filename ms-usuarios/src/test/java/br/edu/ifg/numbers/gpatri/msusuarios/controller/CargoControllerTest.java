package br.edu.ifg.numbers.gpatri.msusuarios.controller;

import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.service.CargoService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração e Segurança para CargoController")
class CargoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CargoService cargoService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID cargoId;
    private UUID permissaoId1;
    private UUID permissaoId2;
    private CargoRequestDTO cargoRequestDTO;
    private CargoResponseDTO cargoResponseDTO;
    private PermissaoResponseDTO permissaoResponseDTO1;
    private PermissaoResponseDTO permissaoResponseDTO2;

    @BeforeEach
    void setUp() {
        cargoId = UUID.randomUUID();
        permissaoId1 = UUID.randomUUID();
        permissaoId2 = UUID.randomUUID();

        permissaoResponseDTO1 = PermissaoResponseDTO.builder()
                .id(permissaoId1)
                .nome("USUARIO_LISTAR")
                .descricao("Listar usuários")
                .categoria("USUARIOS")
                .build();

        permissaoResponseDTO2 = PermissaoResponseDTO.builder()
                .id(permissaoId2)
                .nome("CARGO_LISTAR")
                .descricao("Listar cargos")
                .categoria("CARGOS")
                .build();

        cargoRequestDTO = CargoRequestDTO.builder()
                .nome("OPERADOR")
                .permissoesIds(Set.of(permissaoId1, permissaoId2))
                .build();

        cargoResponseDTO = CargoResponseDTO.builder()
                .id(cargoId)
                .nome("OPERADOR")
                .permissoes(Set.of(permissaoResponseDTO1, permissaoResponseDTO2))
                .build();
    }

    // ==========================================
    // GET /api/v1/cargos
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/cargos - Deve retornar 200 OK com lista de cargos quando possui autoridade CARGO_LISTAR")
    void deveRetornarTodosCargosComAutoridade() throws Exception {
        when(cargoService.buscarTodos()).thenReturn(List.of(cargoResponseDTO));

        mockMvc.perform(get("/api/v1/cargos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(cargoId.toString()))
                .andExpect(jsonPath("$[0].nome").value("OPERADOR"))
                .andExpect(jsonPath("$[0].permissoes.length()").value(2));

        verify(cargoService, times(1)).buscarTodos();
    }

    @Test
    @DisplayName("GET /api/v1/cargos - Deve retornar 403 Forbidden quando não possui autoridade CARGO_LISTAR")
    void deveRetornarForbiddenAoListarCargosSemAutoridade() throws Exception {
        mockMvc.perform(get("/api/v1/cargos")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("USUARIO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Acesso negado. Você não tem permissão para acessar este recurso."));

        verify(cargoService, never()).buscarTodos();
    }

    @Test
    @DisplayName("GET /api/v1/cargos - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoListarCargosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/cargos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuario não autenticado, por favor faça login."));

        verify(cargoService, never()).buscarTodos();
    }

    // ==========================================
    // GET /api/v1/cargos/{id}
    // ==========================================

    @Test
    @DisplayName("GET /api/v1/cargos/{id} - Deve retornar 200 OK quando cargo existe e possui autoridade CARGO_LISTAR")
    void deveRetornarCargoPorIdComAutoridade() throws Exception {
        when(cargoService.findById(cargoId)).thenReturn(cargoResponseDTO);

        mockMvc.perform(get("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cargoId.toString()))
                .andExpect(jsonPath("$.nome").value("OPERADOR"))
                .andExpect(jsonPath("$.permissoes.length()").value(2));

        verify(cargoService, times(1)).findById(cargoId);
    }

    @Test
    @DisplayName("GET /api/v1/cargos/{id} - Deve retornar 404 Not Found quando cargo não existe")
    void deveRetornarNotFoundAoBuscarCargoPorIdInexistente() throws Exception {
        when(cargoService.findById(cargoId)).thenThrow(new ResourceNotFoundException("Cargo não encontrado"));

        mockMvc.perform(get("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cargo não encontrado"));

        verify(cargoService, times(1)).findById(cargoId);
    }

    @Test
    @DisplayName("GET /api/v1/cargos/{id} - Deve retornar 403 Forbidden quando não possui autoridade CARGO_LISTAR")
    void deveRetornarForbiddenAoBuscarCargoPorIdSemAutoridade() throws Exception {
        mockMvc.perform(get("/api/v1/cargos/{id}", cargoId)
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("USUARIO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(cargoService, never()).findById(any());
    }

    @Test
    @DisplayName("GET /api/v1/cargos/{id} - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoBuscarCargoPorIdSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/v1/cargos/{id}", cargoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(cargoService, never()).findById(any());
    }

    // ==========================================
    // POST /api/v1/cargos
    // ==========================================

    @Test
    @DisplayName("POST /api/v1/cargos - Deve retornar 201 Created quando payload é válido e possui autoridade CARGO_CADASTRAR")
    void deveCriarCargoComSucessoQuandoPossuiAutoridade() throws Exception {
        when(cargoService.criarCargo(any(CargoRequestDTO.class))).thenReturn(cargoResponseDTO);

        mockMvc.perform(post("/api/v1/cargos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_CADASTRAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(cargoId.toString()))
                .andExpect(jsonPath("$.nome").value("OPERADOR"));

        verify(cargoService, times(1)).criarCargo(any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/cargos - Deve retornar 400 Bad Request quando payload possui nome em branco ou permissões vazias")
    void deveRetornarBadRequestAoCriarCargoComPayloadInvalido() throws Exception {
        CargoRequestDTO dtoInvalido = CargoRequestDTO.builder()
                .nome("")
                .permissoesIds(Collections.emptySet())
                .build();

        mockMvc.perform(post("/api/v1/cargos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_CADASTRAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(cargoService, never()).criarCargo(any());
    }

    @Test
    @DisplayName("POST /api/v1/cargos - Deve retornar 400 Bad Request quando serviço lança BadRequestException para permissões inexistentes")
    void deveRetornarBadRequestAoCriarCargoComPermissoesInexistentes() throws Exception {
        when(cargoService.criarCargo(any(CargoRequestDTO.class)))
                .thenThrow(new BadRequestException("Uma ou mais permissões informadas não foram encontradas."));

        mockMvc.perform(post("/api/v1/cargos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_CADASTRAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Uma ou mais permissões informadas não foram encontradas."));

        verify(cargoService, times(1)).criarCargo(any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/cargos - Deve retornar 409 Conflict quando cargo com mesmo nome já existe")
    void deveRetornarConflictAoCriarCargoComNomeExistente() throws Exception {
        when(cargoService.criarCargo(any(CargoRequestDTO.class)))
                .thenThrow(new ConflictException("Já existe um cargo cadastrado com este nome."));

        mockMvc.perform(post("/api/v1/cargos")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_CADASTRAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe um cargo cadastrado com este nome."));

        verify(cargoService, times(1)).criarCargo(any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/cargos - Deve retornar 403 Forbidden quando não possui autoridade CARGO_CADASTRAR")
    void deveRetornarForbiddenAoCriarCargoSemAutoridade() throws Exception {
        mockMvc.perform(post("/api/v1/cargos")
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CARGO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(cargoService, never()).criarCargo(any());
    }

    @Test
    @DisplayName("POST /api/v1/cargos - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoCriarCargoSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/v1/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(cargoService, never()).criarCargo(any());
    }

    // ==========================================
    // PATCH /api/v1/cargos/{id}
    // ==========================================

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 200 OK quando atualização é bem-sucedida com autoridade CARGO_EDITAR")
    void deveAtualizarCargoComSucessoQuandoPossuiAutoridade() throws Exception {
        when(cargoService.atualizarCargo(eq(cargoId), any(CargoRequestDTO.class))).thenReturn(cargoResponseDTO);

        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EDITAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cargoId.toString()))
                .andExpect(jsonPath("$.nome").value("OPERADOR"));

        verify(cargoService, times(1)).atualizarCargo(eq(cargoId), any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 400 Bad Request quando payload é inválido")
    void deveRetornarBadRequestAoAtualizarCargoComPayloadInvalido() throws Exception {
        CargoRequestDTO dtoInvalido = CargoRequestDTO.builder()
                .nome("AB") // Menor que 3 caracteres
                .permissoesIds(Set.of(permissaoId1))
                .build();

        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EDITAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(cargoService, never()).atualizarCargo(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 400 Bad Request ao tentar renomear cargo essencial")
    void deveRetornarBadRequestAoAtualizarCargoEssencial() throws Exception {
        when(cargoService.atualizarCargo(eq(cargoId), any(CargoRequestDTO.class)))
                .thenThrow(new BadRequestException("Cargos essenciais do sistema não podem ser renomeados."));

        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EDITAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Cargos essenciais do sistema não podem ser renomeados."));

        verify(cargoService, times(1)).atualizarCargo(eq(cargoId), any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 404 Not Found quando cargo não existe")
    void deveRetornarNotFoundAoAtualizarCargoInexistente() throws Exception {
        when(cargoService.atualizarCargo(eq(cargoId), any(CargoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Cargo não encontrado"));

        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EDITAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cargo não encontrado"));

        verify(cargoService, times(1)).atualizarCargo(eq(cargoId), any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 409 Conflict quando novo nome já está em uso")
    void deveRetornarConflictAoAtualizarCargoComNomeExistente() throws Exception {
        when(cargoService.atualizarCargo(eq(cargoId), any(CargoRequestDTO.class)))
                .thenThrow(new ConflictException("Já existe outro cargo cadastrado com este nome."));

        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EDITAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe outro cargo cadastrado com este nome."));

        verify(cargoService, times(1)).atualizarCargo(eq(cargoId), any(CargoRequestDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 403 Forbidden quando não possui autoridade CARGO_EDITAR")
    void deveRetornarForbiddenAoAtualizarCargoSemAutoridade() throws Exception {
        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CARGO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(cargoService, never()).atualizarCargo(any(), any());
    }

    @Test
    @DisplayName("PATCH /api/v1/cargos/{id} - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoAtualizarCargoSemAutenticacao() throws Exception {
        mockMvc.perform(patch("/api/v1/cargos/{id}", cargoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cargoRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(cargoService, never()).atualizarCargo(any(), any());
    }

    // ==========================================
    // DELETE /api/v1/cargos/{id}
    // ==========================================

    @Test
    @DisplayName("DELETE /api/v1/cargos/{id} - Deve retornar 204 No Content quando exclusão é bem-sucedida com autoridade CARGO_EXCLUIR")
    void deveDeletarCargoComSucessoQuandoPossuiAutoridade() throws Exception {
        doNothing().when(cargoService).deletarCargo(cargoId);

        mockMvc.perform(delete("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EXCLUIR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cargoService, times(1)).deletarCargo(cargoId);
    }

    @Test
    @DisplayName("DELETE /api/v1/cargos/{id} - Deve retornar 400 Bad Request ao tentar excluir cargo essencial")
    void deveRetornarBadRequestAoDeletarCargoEssencial() throws Exception {
        doThrow(new BadRequestException("Cargos essenciais do sistema não podem ser excluídos."))
                .when(cargoService).deletarCargo(cargoId);

        mockMvc.perform(delete("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EXCLUIR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Cargos essenciais do sistema não podem ser excluídos."));

        verify(cargoService, times(1)).deletarCargo(cargoId);
    }

    @Test
    @DisplayName("DELETE /api/v1/cargos/{id} - Deve retornar 404 Not Found quando cargo não existe")
    void deveRetornarNotFoundAoDeletarCargoInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Cargo não encontrado"))
                .when(cargoService).deletarCargo(cargoId);

        mockMvc.perform(delete("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EXCLUIR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cargo não encontrado"));

        verify(cargoService, times(1)).deletarCargo(cargoId);
    }

    @Test
    @DisplayName("DELETE /api/v1/cargos/{id} - Deve retornar 409 Conflict quando cargo possui associações de chave estrangeira")
    void deveRetornarConflictAoDeletarCargoComAssociacoes() throws Exception {
        doThrow(new ConflictException("Violação de integridade referencial: não é possível excluir ou alterar o recurso pois ele está vinculado a outros registros."))
                .when(cargoService).deletarCargo(cargoId);

        mockMvc.perform(delete("/api/v1/cargos/{id}", cargoId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("CARGO_EXCLUIR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Violação de integridade referencial: não é possível excluir ou alterar o recurso pois ele está vinculado a outros registros."));

        verify(cargoService, times(1)).deletarCargo(cargoId);
    }

    @Test
    @DisplayName("DELETE /api/v1/cargos/{id} - Deve retornar 403 Forbidden quando não possui autoridade CARGO_EXCLUIR")
    void deveRetornarForbiddenAoDeletarCargoSemAutoridade() throws Exception {
        mockMvc.perform(delete("/api/v1/cargos/{id}", cargoId)
                        .with(user("usuario").authorities(new SimpleGrantedAuthority("CARGO_LISTAR")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(cargoService, never()).deletarCargo(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/cargos/{id} - Deve retornar 401 Unauthorized quando não autenticado")
    void deveRetornarUnauthorizedAoDeletarCargoSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/api/v1/cargos/{id}", cargoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verify(cargoService, never()).deletarCargo(any());
    }
}
