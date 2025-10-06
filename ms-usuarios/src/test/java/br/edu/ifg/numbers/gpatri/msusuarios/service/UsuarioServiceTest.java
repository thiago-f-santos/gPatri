package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UsuarioCargoUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.UsuarioMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UUID usuarioId;
    private UUID cargoId;
    private UserRequestDTO userRequestDTO;
    private Usuario usuario;
    private UserResponseDTO userResponseDTO;
    private Cargo cargoComum;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        cargoId = UUID.randomUUID();

        userRequestDTO = new UserRequestDTO("Nome", "Sobrenome", "teste@gmail.com", "12345678");

        Set<PermissaoEnum> permissoes = Set.of(PermissaoEnum.EMPRESTIMO_SOLICITAR);
        cargoComum = new Cargo("USUARIO_COMUM");
        cargoComum.setId(cargoId);
        cargoComum.setPermissoes(permissoes);

        usuario = new Usuario(usuarioId, "Nome", "Sobrenome", "teste@gmail.com", userRequestDTO.getSenha(), cargoComum);
        usuario.setId(usuarioId);

        userResponseDTO = new UserResponseDTO(
                usuarioId,
                "Nome",
                "Sobrenome",
                "teste@gmail.com",
                "USUARIO_COMUM",
                cargoId
        );

        userUpdateDTO = new UserUpdateDTO("NomeNovo", "SobrenomeNovo", "novoemail@gmail.com");
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void criarUsuario() {

        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(cargoRepository.findByNome("USUARIO_COMUM")).thenReturn(Optional.of(cargoComum));

        when(usuarioMapper.toEntity(userRequestDTO)).thenReturn(usuario);
        when(passwordEncoder.encode(userRequestDTO.getSenha())).thenReturn("senhaCriptografada");
        when(userRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDto(usuario)).thenReturn(userResponseDTO);

        UserResponseDTO resultado = usuarioService.criarUsuario(userRequestDTO);

        assertNotNull(resultado);
        assertEquals(userResponseDTO.getEmail(), resultado.getEmail());
        assertEquals("USUARIO_COMUM", resultado.getCargo());

        verify(userRepository, times(1)).findByEmail(userRequestDTO.getEmail());
        verify(cargoRepository, times(1)).findByNome("USUARIO_COMUM");
        verify(passwordEncoder, times(1)).encode(userRequestDTO.getSenha());
        verify(userRepository, times(1)).save(any(Usuario.class));
        verify(usuarioMapper, times(1)).toEntity(userRequestDTO);
        verify(usuarioMapper, times(1)).toDto(usuario);
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar criar usuário com email já existente")
    void criarUsuarioEmailJaExistente() {

        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(ConflictException.class, () -> usuarioService.criarUsuario(userRequestDTO));

        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o cargo não for encontrado ao criar usuário")
    void criarUsuarioCargoNaoEncontrado() {

        when(userRepository.findByEmail(userRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(cargoRepository.findByNome("USUARIO_COMUM")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.criarUsuario(userRequestDTO));

        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void buscarUsuarioPorId() {

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDto(usuario)).thenReturn(userResponseDTO);

        UserResponseDTO response = usuarioService.buscarPid(usuarioId);

        assertNotNull(response);
        assertEquals(userResponseDTO.getEmail(), response.getEmail());

        verify(userRepository, times(1)).findById(usuarioId);
        verify(usuarioMapper, times(1)).toDto(usuario);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar usuário por ID inexistente")
    void buscarUsuarioPorIdInexistente() {

        when(userRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPid(usuarioId));

        verify(usuarioMapper, never()).toDto(any(Usuario.class));
    }

//    @Test
//    @DisplayName("Deve retornar uma lista de todos os usuários com sucesso")
//    void buscarTodosUsuarios() {
//
//        List<Usuario> usuarios = Collections.singletonList(this.usuario);
//        List<UserResponseDTO> usuariosDTOs = Collections.singletonList(this.userResponseDTO);
//
//        when(userRepository.findAll()).thenReturn(usuarios);
//        when(usuarioMapper.toDtoList(usuarios)).thenReturn(usuariosDTOs);
//
//        List<UserResponseDTO> resultado = usuarioService.buscarTodos();
//
//        assertNotNull(resultado);
//        assertFalse(resultado.isEmpty());
//        assertEquals(usuariosDTOs.size(), resultado.size());
//
//        verify(userRepository, times(1)).findAll();
//        verify(usuarioMapper, times(1)).toDtoList(usuarios);
//    }

//    @Test
//    @DisplayName("Deve retornar uma lista vazia quando não houver usuários")
//    void buscarTodosUsuariosVazio() {
//
//        List<Usuario> listaVazia = Collections.emptyList();
//
//        when(userRepository.findAll()).thenReturn(listaVazia);
//        when(usuarioMapper.toDtoList(listaVazia)).thenReturn(Collections.emptyList());
//
//        List<UserResponseDTO> resultado = usuarioService.buscarTodos();
//
//        assertNotNull(resultado);
//        assertTrue(resultado.isEmpty());
//
//        verify(userRepository, times(1)).findAll();
//        verify(usuarioMapper, times(1)).toDtoList(listaVazia);
//    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void atualizarUsuario() {

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDto(usuario)).thenReturn(userResponseDTO);

        UserResponseDTO resultado = usuarioService.atualizarUsuario(usuarioId, userUpdateDTO);

        assertNotNull(resultado);
        assertEquals(userResponseDTO.getEmail(), resultado.getEmail());

        verify(userRepository, times(1)).findById(usuarioId);
        verify(userRepository, times(1)).findByEmail(userUpdateDTO.getEmail());
        verify(userRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atualizar usuário inexistente")
    void atualizarUsuarioInexistente() {

        when(userRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizarUsuario(usuarioId, userUpdateDTO));

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar atualizar usuário com email já existente")
    void atualizarUsuarioEmailExistente() {

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(UUID.randomUUID());
        usuarioExistente.setEmail(userUpdateDTO.getEmail());

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(Optional.of(usuarioExistente));

        assertThrows(ConflictException.class, () -> usuarioService.atualizarUsuario(usuarioId, userUpdateDTO));

        verify(userRepository, times(1)).findById(usuarioId);
        verify(userRepository, times(1)).findByEmail(userUpdateDTO.getEmail());
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    void deletarUsuario() {

        when(userRepository.existsById(usuarioId)).thenReturn(true);

        assertDoesNotThrow(() -> usuarioService.deletarUsuario(usuarioId));

        verify(userRepository, times(1)).existsById(usuarioId);
        verify(userRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar usuário inexistente")
    void deletarUsuarioInexistente() {

        when(userRepository.existsById(usuarioId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deletarUsuario(usuarioId));

        verify(userRepository, times(1)).existsById(usuarioId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar ConflictException ao tentar deletar usuário vinculado a um cargo")
    void deletarUsuarioVinculadoACargo() {

        when(userRepository.existsById(usuarioId)).thenReturn(true);
        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(usuarioId);

        assertThrows(ConflictException.class, () -> usuarioService.deletarUsuario(usuarioId));

        verify(userRepository, times(1)).existsById(usuarioId);
        verify(userRepository, times(1)).deleteById(usuarioId);
    }

    @Test
    @DisplayName("Deve atribuir um cargo ao usuário com sucesso")
    void atribuirCargoAoUsuario() {

        Cargo novoCargo = new Cargo("NOVO_CARGO");
        novoCargo.setId(UUID.randomUUID());

        UsuarioCargoUpdateDTO usuarioCargoUpdateDTO = new UsuarioCargoUpdateDTO(novoCargo.getId());

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(cargoRepository.findById(novoCargo.getId())).thenReturn(Optional.of(novoCargo));
        when(userRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDto(any(Usuario.class))).thenReturn(userResponseDTO);

        UserResponseDTO response = usuarioService.atribuirCargo(usuarioId, usuarioCargoUpdateDTO);

        assertNotNull(response);
        assertEquals(novoCargo.getId(), usuario.getCargo().getId());

        verify(userRepository, times(1)).findById(usuarioId);
        verify(cargoRepository, times(1)).findById(novoCargo.getId());
        verify(userRepository, times(1)).save(any(Usuario.class));
        verify(usuarioMapper, times(1)).toDto(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atribuir cargo a usuário inexistente")
    void atribuirCargoAoUsuarioInexistente() {

        UUID cargoId = UUID.randomUUID();
        UsuarioCargoUpdateDTO usuarioCargoUpdateDTO = new UsuarioCargoUpdateDTO(cargoId);

        when(userRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atribuirCargo(usuarioId, usuarioCargoUpdateDTO));

        verify(cargoRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar atribuir cargo inexistente a usuário")
    void atribuirCargoInexistenteAoUsuario() {

        UUID cargoId = UUID.randomUUID();
        UsuarioCargoUpdateDTO usuarioCargoUpdateDTO = new UsuarioCargoUpdateDTO(cargoId);

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(cargoRepository.findById(usuarioCargoUpdateDTO.getIdCargo())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atribuirCargo(usuarioId, usuarioCargoUpdateDTO));

        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException ao tentar atribuir o mesmo cargo ao usuário")
    void atribuirMesmoCargoAoUsuario() {

        Cargo cargoExistente = usuario.getCargo();
        UsuarioCargoUpdateDTO usuarioCargoUpdateDTO = new UsuarioCargoUpdateDTO(cargoExistente.getId());

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(cargoRepository.findById(usuarioCargoUpdateDTO.getIdCargo())).thenReturn(Optional.of(cargoExistente));

        assertThrows(BadRequestException.class, () -> usuarioService.atribuirCargo(usuarioId, usuarioCargoUpdateDTO));

        verify(userRepository, never()).save(any(Usuario.class));
    }
}
