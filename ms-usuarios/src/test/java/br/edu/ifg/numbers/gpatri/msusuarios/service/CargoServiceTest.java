package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.CargoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o CargoService")
class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private CargoMapper cargoMapper;

    @InjectMocks
    private CargoService cargoService;

    private CargoRequestDTO cargoRequestDTO;
    private Cargo cargo;
    private CargoResponseDTO cargoResponseDTO;
    private UUID cargoId;
    private Set<String> permissoesString;
    private Set<PermissaoEnum> permissoesEnum;

    @BeforeEach
    void setUp() {
        cargoId = UUID.randomUUID();
        permissoesString = Set.of("USUARIO_CADASTRAR", "CARGO_LISTAR", "USUARIO_LISTAR", "USUARIO_EDITAR", "USUARIO_EXCLUIR");
        permissoesEnum = permissoesString.stream()
                .map(PermissaoEnum::valueOf)
                .collect(Collectors.toSet());

        cargoRequestDTO = new CargoRequestDTO("NOME_CARGO", permissoesString);
        cargo = new Cargo("NOME_CARGO");
        cargo.setId(cargoId);
        cargo.setPermissoes(permissoesEnum);
        cargoResponseDTO = new CargoResponseDTO(cargoId, "NOME_CARGO", permissoesString);
    }

    @Test
    @DisplayName("Deve cadastrar um cargo com sucesso")
    void cadastrarCargo() {

        when(cargoRepository.findByNome(cargoRequestDTO.getNome())).thenReturn(java.util.Optional.empty());

        when(cargoMapper.toEntity(cargoRequestDTO)).thenReturn(cargo);
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);
        when(cargoMapper.toDto(cargo)).thenReturn(cargoResponseDTO);

        CargoResponseDTO response = cargoService.criarCargo(cargoRequestDTO);

        assertNotNull(response);
        assertEquals(cargoResponseDTO.getNome(), response.getNome());
        assertEquals(cargoResponseDTO.getPermissoes(), response.getPermissoes());

        verify(cargoRepository, times(1)).findByNome(cargoRequestDTO.getNome());
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar conflito ao cadastrar cargo com nome já existente")
    void cadastrarCargoConflito() {

        when(cargoRepository.findByNome(cargoRequestDTO.getNome())).thenReturn(Optional.of(cargo));

        assertThrows(ConflictException.class, () -> cargoService.criarCargo(cargoRequestDTO));

        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequest ao cadastrar cargo com permissões inválidas")
    void cadastrarCargoPermissoesInvalidas() {

        when(cargoRepository.findByNome(cargoRequestDTO.getNome())).thenReturn(Optional.empty());

        doThrow(new BadRequestException("Permissão inválida encontrada: 'PERMISSAO_INEXISTENTE'"))
                .when(cargoMapper).toEntity(any(CargoRequestDTO.class));

        assertThrows(BadRequestException.class, () -> cargoService.criarCargo(cargoRequestDTO));

        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve buscar cargo por ID com sucesso")
    void buscarCargoPorId() {

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoMapper.toDto(cargo)).thenReturn(cargoResponseDTO);

        CargoResponseDTO response = cargoService.buscarPorId(cargoId);

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

        assertThrows(ResourceNotFoundException.class, () -> cargoService.buscarPorId(cargoId));

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoMapper, never()).toDto(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve retornar todos os cargos com sucesso")
    void buscarTodosCargos() {

        Cargo outroCargo = new Cargo("OUTRO_CARGO");
        outroCargo.setId(UUID.randomUUID());
        outroCargo.setPermissoes(Collections.emptySet());
        List<Cargo> cargos = Arrays.asList(this.cargo, outroCargo);

        List<CargoResponseDTO> listaRespostas = cargos.stream()
                .map(c -> new CargoResponseDTO(c.getId(), c.getNome(), c.getPermissoes().stream().map(PermissaoEnum::name).collect(Collectors.toSet())))
                .collect(Collectors.toList());

        when(cargoRepository.findAll()).thenReturn(cargos);
        when(cargoMapper.toDtoList(cargos)).thenReturn(listaRespostas);

        List<CargoResponseDTO> response = cargoService.buscarTodos();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(listaRespostas.size(), response.size());

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
    @DisplayName("Deve atualizar um cargo com sucesso")
    void atualizarCargo() {

        CargoRequestDTO dtoAtualizacao = new CargoRequestDTO("CARGO_ATUALIZADO", permissoesString);
        Cargo cargoAtualizado = new Cargo("CARGO_ATUALIZADO");
        cargoAtualizado.setId(cargoId);
        cargoAtualizado.setPermissoes(permissoesEnum);

        CargoResponseDTO dtoResponseAtualizado = new CargoResponseDTO(cargoId, "CARGO_ATUALIZADO", permissoesString);

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByNome(dtoAtualizacao.getNome())).thenReturn(Optional.empty());

        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargoAtualizado);
        when(cargoMapper.toDto(cargoAtualizado)).thenReturn(dtoResponseAtualizado);

        CargoResponseDTO response = cargoService.atualizarCargo(cargoId, dtoAtualizacao);

        assertNotNull(response);
        assertEquals(dtoResponseAtualizado.getNome(), response.getNome());
        assertEquals(dtoResponseAtualizado.getPermissoes(), response.getPermissoes());

        verify(cargoRepository, times(1)).findById(cargoId);
        verify(cargoRepository, times(1)).findByNome(dtoAtualizacao.getNome());
        verify(cargoMapper, times(1)).updateEntityFromDto(eq(dtoAtualizacao), any(Cargo.class));
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao atualizar cargo com ID inexistente")
    void atualizarCargoInexistente() {

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cargoService.atualizarCargo(cargoId, cargoRequestDTO));

        verify(cargoRepository, never()).findByNome(anyString());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando o novo nome do cargo já existe")
    void deveLancarConflictExceptionQuandoNovoNomeDoCargoJaExiste() {

        CargoRequestDTO dtoAtualizacao = new CargoRequestDTO("NOME_JA_EXISTENTE", permissoesString);

        Cargo cargoExistenteComNome = new Cargo("NOME_JA_EXISTENTE");
        cargoExistenteComNome.setId(UUID.randomUUID());

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByNome(dtoAtualizacao.getNome())).thenReturn(Optional.of(cargoExistenteComNome));

        assertThrows(ConflictException.class, () -> cargoService.atualizarCargo(cargoId, dtoAtualizacao));

        verify(cargoRepository, times(1)).findByNome(dtoAtualizacao.getNome());
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando as permissões de atualização são inválidas")
    void deveLancarBadRequestExceptionQuandoPermissoesDeAtualizacaoSaoInvalidas() {

        CargoRequestDTO dtoAtualizacao = new CargoRequestDTO("CARGO_ATUALIZADO", Set.of("PERMISSAO_INVALIDA"));

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByNome(dtoAtualizacao.getNome())).thenReturn(Optional.empty());

        doThrow(BadRequestException.class).when(cargoMapper).updateEntityFromDto(eq(dtoAtualizacao), any(Cargo.class));

        assertThrows(BadRequestException.class, () -> cargoService.atualizarCargo(cargoId, dtoAtualizacao));

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
    @DisplayName("Deve lançar ConflictException quando o cargo não pode ser deletado devido a FK")
    void deveLancarConflictExceptionQuandoCargoTemAssociacoes() {

        when(cargoRepository.existsById(cargoId)).thenReturn(true);

        doThrow(DataIntegrityViolationException.class).when(cargoRepository).deleteById(cargoId);

        assertThrows(ConflictException.class, () -> cargoService.deletarCargo(cargoId));

        verify(cargoRepository, times(1)).existsById(cargoId);
        verify(cargoRepository, times(1)).deleteById(cargoId);
    }
}
