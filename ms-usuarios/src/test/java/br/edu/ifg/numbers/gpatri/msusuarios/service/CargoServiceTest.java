package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.CargoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o CargoService")
class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private CargoMapper cargoMapper;

    @InjectMocks
    private CargoService cargoService;

    private Cargo cargo;
    private CargoRequestDTO cargoRequestDTO;
    private CargoResponseDTO cargoResponseDTO;
    private Permissao permissao1;
    private Permissao permissao2;
    private PermissaoResponseDTO permissaoResponseDTO1;
    private PermissaoResponseDTO permissaoResponseDTO2;
    private UUID cargoId;
    private UUID permissaoId1;
    private UUID permissaoId2;
    private Set<UUID> permissoesIds;

    @BeforeEach
    void setUp() {
        cargoId = UUID.randomUUID();
        permissaoId1 = UUID.randomUUID();
        permissaoId2 = UUID.randomUUID();
        permissoesIds = Set.of(permissaoId1, permissaoId2);

        permissao1 = Permissao.builder()
                .id(permissaoId1)
                .nome("USUARIO_LISTAR")
                .descricao("Listar usuários")
                .categoria("USUARIOS")
                .build();

        permissao2 = Permissao.builder()
                .id(permissaoId2)
                .nome("CARGO_LISTAR")
                .descricao("Listar cargos")
                .categoria("CARGOS")
                .build();

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

        cargo = Cargo.builder()
                .id(cargoId)
                .nome("ADMINISTRADOR")
                .permissoes(new HashSet<>(Set.of(permissao1, permissao2)))
                .build();

        cargoRequestDTO = CargoRequestDTO.builder()
                .nome("ADMINISTRADOR")
                .permissoesIds(permissoesIds)
                .build();

        cargoResponseDTO = CargoResponseDTO.builder()
                .id(cargoId)
                .nome("ADMINISTRADOR")
                .permissoes(Set.of(permissaoResponseDTO1, permissaoResponseDTO2))
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar um cargo com sucesso")
    void cadastrarCargo() {
        when(cargoRepository.findByNome(cargoRequestDTO.getNome())).thenReturn(Optional.empty());
        when(permissaoRepository.findAllById(permissoesIds)).thenReturn(List.of(permissao1, permissao2));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);
        when(cargoMapper.toDto(cargo)).thenReturn(cargoResponseDTO);

        CargoResponseDTO response = cargoService.criarCargo(cargoRequestDTO);

        assertNotNull(response);
        assertEquals(cargoResponseDTO.getNome(), response.getNome());
        assertEquals(cargoResponseDTO.getPermissoes(), response.getPermissoes());

        verify(cargoRepository, times(1)).findByNome(cargoRequestDTO.getNome());
        verify(permissaoRepository, times(1)).findAllById(permissoesIds);
        verify(cargoRepository, times(1)).save(any(Cargo.class));
        verify(cargoMapper, times(1)).toDto(cargo);
    }

    @Test
    @DisplayName("Deve lançar conflito ao cadastrar cargo com nome já existente")
    void cadastrarCargoConflito() {
        when(cargoRepository.findByNome(cargoRequestDTO.getNome())).thenReturn(Optional.of(cargo));

        assertThrows(ConflictException.class, () -> cargoService.criarCargo(cargoRequestDTO));

        verify(cargoRepository, times(1)).findByNome(cargoRequestDTO.getNome());
        verify(permissaoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequest ao cadastrar cargo com permissões nulas")
    void cadastrarCargoPermissoesNulas() {
        CargoRequestDTO dtoComPermissoesNulas = CargoRequestDTO.builder()
                .nome("NOVO_CARGO")
                .permissoesIds(null)
                .build();

        when(cargoRepository.findByNome("NOVO_CARGO")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cargoService.criarCargo(dtoComPermissoesNulas));

        verify(cargoRepository, times(1)).findByNome("NOVO_CARGO");
        verify(permissaoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequest ao cadastrar cargo com permissões vazias")
    void cadastrarCargoPermissoesVazias() {
        CargoRequestDTO dtoComPermissoesVazias = CargoRequestDTO.builder()
                .nome("NOVO_CARGO")
                .permissoesIds(Collections.emptySet())
                .build();

        when(cargoRepository.findByNome("NOVO_CARGO")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cargoService.criarCargo(dtoComPermissoesVazias));

        verify(cargoRepository, times(1)).findByNome("NOVO_CARGO");
        verify(permissaoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequest ao cadastrar cargo com permissões inexistentes no banco")
    void cadastrarCargoPermissoesInexistentes() {
        when(cargoRepository.findByNome(cargoRequestDTO.getNome())).thenReturn(Optional.empty());
        when(permissaoRepository.findAllById(permissoesIds)).thenReturn(List.of(permissao1)); // Retorna apenas 1 de 2

        BadRequestException ex = assertThrows(BadRequestException.class, () -> cargoService.criarCargo(cargoRequestDTO));
        assertTrue(ex.getMessage().contains("não foram encontradas"));

        verify(cargoRepository, times(1)).findByNome(cargoRequestDTO.getNome());
        verify(permissaoRepository, times(1)).findAllById(permissoesIds);
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve buscar cargo por ID com sucesso")
    void buscarCargoPorId() {
        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoMapper.toDto(cargo)).thenReturn(cargoResponseDTO);

        CargoResponseDTO response = cargoService.findById(cargoId);

        assertNotNull(response);
        assertEquals(cargoResponseDTO.getId(), response.getId());
        assertEquals(cargoResponseDTO.getNome(), response.getNome());

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoMapper, times(1)).toDto(cargo);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar cargo por ID inexistente")
    void buscarCargoPorIdInexistente() {
        when(cargoRepository.findById(cargoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cargoService.findById(cargoId));

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoMapper, never()).toDto(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve buscar cargo por nome com sucesso")
    void buscarCargoPorNome() {
        when(cargoRepository.findByNome("ADMINISTRADOR")).thenReturn(Optional.of(cargo));
        when(cargoMapper.toDto(cargo)).thenReturn(cargoResponseDTO);

        CargoResponseDTO response = cargoService.buscarPorNome("ADMINISTRADOR");

        assertNotNull(response);
        assertEquals(cargoResponseDTO.getNome(), response.getNome());

        verify(cargoRepository, times(1)).findByNome("ADMINISTRADOR");
        verify(cargoMapper, times(1)).toDto(cargo);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar cargo por nome inexistente")
    void buscarCargoPorNomeInexistente() {
        when(cargoRepository.findByNome("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cargoService.buscarPorNome("INEXISTENTE"));

        verify(cargoRepository, times(1)).findByNome("INEXISTENTE");
        verify(cargoMapper, never()).toDto(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve retornar todos os cargos com sucesso")
    void buscarTodosCargos() {
        Cargo outroCargo = Cargo.builder()
                .id(UUID.randomUUID())
                .nome("OPERADOR")
                .permissoes(Collections.emptySet())
                .build();
        List<Cargo> cargos = List.of(this.cargo, outroCargo);

        CargoResponseDTO outroCargoDTO = CargoResponseDTO.builder()
                .id(outroCargo.getId())
                .nome("OPERADOR")
                .permissoes(Collections.emptySet())
                .build();
        List<CargoResponseDTO> listaRespostas = List.of(this.cargoResponseDTO, outroCargoDTO);

        when(cargoRepository.findAll()).thenReturn(cargos);
        when(cargoMapper.toDtoList(cargos)).thenReturn(listaRespostas);

        List<CargoResponseDTO> response = cargoService.buscarTodos();

        assertNotNull(response);
        assertEquals(2, response.size());

        verify(cargoRepository, times(1)).findAll();
        verify(cargoMapper, times(1)).toDtoList(cargos);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não houver cargos cadastrados")
    void buscarTodosCargosVazio() {
        when(cargoRepository.findAll()).thenReturn(Collections.emptyList());
        when(cargoMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<CargoResponseDTO> response = cargoService.buscarTodos();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(cargoRepository, times(1)).findAll();
        verify(cargoMapper, times(1)).toDtoList(Collections.emptyList());
    }

    @Test
    @DisplayName("Deve atualizar um cargo com sucesso alterando o nome")
    void atualizarCargo() {
        UUID novoPermissaoId = UUID.randomUUID();
        Permissao novaPermissao = Permissao.builder().id(novoPermissaoId).nome("PATRIMONIO_LISTAR").build();
        Set<UUID> novosIds = Set.of(novoPermissaoId);

        CargoRequestDTO dtoAtualizacao = CargoRequestDTO.builder()
                .nome("CARGO_ATUALIZADO")
                .permissoesIds(novosIds)
                .build();

        Cargo cargoAtualizado = Cargo.builder()
                .id(cargoId)
                .nome("CARGO_ATUALIZADO")
                .permissoes(Set.of(novaPermissao))
                .build();

        CargoResponseDTO dtoResponseAtualizado = CargoResponseDTO.builder()
                .id(cargoId)
                .nome("CARGO_ATUALIZADO")
                .permissoes(Set.of(PermissaoResponseDTO.builder().id(novoPermissaoId).nome("PATRIMONIO_LISTAR").build()))
                .build();

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByNome(dtoAtualizacao.getNome())).thenReturn(Optional.empty());
        when(permissaoRepository.findAllById(novosIds)).thenReturn(List.of(novaPermissao));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargoAtualizado);
        when(cargoMapper.toDto(cargoAtualizado)).thenReturn(dtoResponseAtualizado);

        CargoResponseDTO response = cargoService.atualizarCargo(cargoId, dtoAtualizacao);

        assertNotNull(response);
        assertEquals(dtoResponseAtualizado.getNome(), response.getNome());

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoRepository, times(1)).findByNome(dtoAtualizacao.getNome());
        verify(permissaoRepository, times(1)).findAllById(novosIds);
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve atualizar um cargo com sucesso mantendo o mesmo nome")
    void atualizarCargoMantendoMesmoNome() {
        CargoRequestDTO dtoAtualizacao = CargoRequestDTO.builder()
                .nome("ADMINISTRADOR")
                .permissoesIds(permissoesIds)
                .build();

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(permissaoRepository.findAllById(permissoesIds)).thenReturn(List.of(permissao1, permissao2));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);
        when(cargoMapper.toDto(cargo)).thenReturn(cargoResponseDTO);

        CargoResponseDTO response = cargoService.atualizarCargo(cargoId, dtoAtualizacao);

        assertNotNull(response);
        assertEquals(cargoResponseDTO.getNome(), response.getNome());

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoRepository, never()).findByNome(anyString());
        verify(permissaoRepository, times(1)).findAllById(permissoesIds);
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar cargo com ID inexistente")
    void atualizarCargoInexistente() {
        when(cargoRepository.findById(cargoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cargoService.atualizarCargo(cargoId, cargoRequestDTO));

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoRepository, never()).findByNome(anyString());
        verify(permissaoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando o novo nome do cargo já existe")
    void deveLancarConflictExceptionQuandoNovoNomeDoCargoJaExiste() {
        CargoRequestDTO dtoAtualizacao = CargoRequestDTO.builder()
                .nome("NOME_JA_EXISTENTE")
                .permissoesIds(permissoesIds)
                .build();

        Cargo cargoExistenteComNome = Cargo.builder()
                .id(UUID.randomUUID())
                .nome("NOME_JA_EXISTENTE")
                .build();

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByNome(dtoAtualizacao.getNome())).thenReturn(Optional.of(cargoExistenteComNome));

        assertThrows(ConflictException.class, () -> cargoService.atualizarCargo(cargoId, dtoAtualizacao));

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoRepository, times(1)).findByNome(dtoAtualizacao.getNome());
        verify(permissaoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando as permissões de atualização são nulas ou vazias")
    void deveLancarBadRequestExceptionQuandoPermissoesDeAtualizacaoSaoVazias() {
        CargoRequestDTO dtoAtualizacao = CargoRequestDTO.builder()
                .nome("ADMINISTRADOR")
                .permissoesIds(Collections.emptySet())
                .build();

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));

        assertThrows(BadRequestException.class, () -> cargoService.atualizarCargo(cargoId, dtoAtualizacao));

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(permissaoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando as permissões de atualização não existem no banco")
    void deveLancarBadRequestExceptionQuandoPermissoesDeAtualizacaoSaoInvalidas() {
        UUID idInexistente = UUID.randomUUID();
        Set<UUID> idsInexistentes = Set.of(idInexistente);

        CargoRequestDTO dtoAtualizacao = CargoRequestDTO.builder()
                .nome("ADMINISTRADOR")
                .permissoesIds(idsInexistentes)
                .build();

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(permissaoRepository.findAllById(idsInexistentes)).thenReturn(Collections.emptyList());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> cargoService.atualizarCargo(cargoId, dtoAtualizacao));
        assertTrue(ex.getMessage().contains("não foram encontradas"));

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(permissaoRepository, times(1)).findAllById(idsInexistentes);
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve deletar um cargo com sucesso quando ele existe")
    void deveDeletarCargoComSucessoQuandoExiste() {
        when(cargoRepository.existsById(cargoId)).thenReturn(true);

        assertDoesNotThrow(() -> cargoService.deletarCargo(cargoId));

        verify(cargoRepository, times(1)).existsById(cargoId);
        verify(cargoRepository, times(1)).deleteById(cargoId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o cargo a ser deletado não existe")
    void deveLancarResourceNotFoundExceptionQuandoCargoParaDeletarNaoExiste() {
        when(cargoRepository.existsById(cargoId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cargoService.deletarCargo(cargoId));

        verify(cargoRepository, times(1)).existsById(cargoId);
        verify(cargoRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando o cargo não pode ser deletado devido a FK com usuários")
    void deveLancarConflictExceptionQuandoCargoTemAssociacoes() {
        when(cargoRepository.existsById(cargoId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(cargoRepository).deleteById(cargoId);

        assertThrows(ConflictException.class, () -> cargoService.deletarCargo(cargoId));

        verify(cargoRepository, times(1)).existsById(cargoId);
        verify(cargoRepository, times(1)).deleteById(cargoId);
    }
}
