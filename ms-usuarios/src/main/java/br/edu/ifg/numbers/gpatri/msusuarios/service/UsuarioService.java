package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UsuarioCargoUpdateDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.UsuarioMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UserRepository userRepository, CargoRepository cargoRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO criarUsuario(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new ConflictException(String.format("O email '%s' já está cadastrado por outro usuário.", userRequestDTO.getEmail()));
        }

        Cargo cargo = cargoRepository.findByNome("Usuário")
                .orElseThrow(() -> new ResourceNotFoundException("Cargo 'Usuário' não encontrado."));

        Usuario usuario = usuarioMapper.toEntity(userRequestDTO);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setCargo(cargo);

        usuario = userRepository.save(usuario);

        return usuarioMapper.toDto(usuario);
    }

    public UserResponseDTO buscarPid(UUID id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Usuário de ID '%s' não encontrado.", id)));
        return usuarioMapper.toDto(usuario);
    }

    public Page<UserResponseDTO> buscarTodos(Pageable pageable) {
        Page<Usuario> usuarios = userRepository.findAll(pageable);
        return usuarios.map(usuarioMapper::toDto);
    }

    @Transactional
    public UserResponseDTO atualizarUsuario(UUID id, UserUpdateDTO userUpdateDTO) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Usuário de ID '%s' não encontrado.", id)));

        if (userUpdateDTO.getEmail() != null && !usuario.getEmail().equals(userUpdateDTO.getEmail())) {
            if (userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent()) {
                throw new ConflictException(String.format("O email '%s' já está cadastrado por outro usuário.", userUpdateDTO.getEmail()));
            }
        }

        usuarioMapper.updateEntityFromDto(userUpdateDTO, usuario);
        userRepository.save(usuario);
        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Usuário de ID '%s' não encontrado.", id));
        }

        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Não foi possível deletar o usuário de ID '%s'. Existem registros vinculados a este usuários.", id));
        }
    }

    @Transactional
    public UserResponseDTO atribuirCargo(UUID id, UsuarioCargoUpdateDTO usuarioCargoUpdateDTO) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Usuário de ID '%s' não encontrado.", id)));

        Cargo cargo = cargoRepository.findById(usuarioCargoUpdateDTO.getIdCargo())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", usuarioCargoUpdateDTO.getIdCargo())));

        if (usuario.getCargo() != null && !usuario.getCargo().getId().equals(cargo.getId())) {
            usuario.setCargo(cargo);
        }

        usuario = userRepository.save(usuario);

        return usuarioMapper.toDto(usuario);
    }

}
