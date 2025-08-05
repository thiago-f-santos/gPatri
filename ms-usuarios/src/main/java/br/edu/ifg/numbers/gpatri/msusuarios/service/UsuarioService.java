package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.List;
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
        //Verificar se ja existe um usuário com o mesmo email;
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new ConflictException("O email '" + userRequestDTO.getEmail() + "' já está cadastrado por outro usuário.");
        }

        Cargo cargo = cargoRepository.findByNome("USUARIO_COMUM")
                .orElseThrow(() -> new ResourceNotFoundException("Cargo 'USUARIO_COMUM' não encontrado."));

        Usuario usuario = usuarioMapper.toEntity(userRequestDTO);

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        usuario.setCargo(cargo);

        Usuario novoUsuario = userRepository.save(usuario);

        return usuarioMapper.toDto(novoUsuario);
    }

    public UserResponseDTO buscarPid(UUID id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário de ID '" + id + "' não encontrado."));
        return usuarioMapper.toDto(usuario);
    }

    public List<UserResponseDTO> buscarTodos() {
        //Buscar todos os usuários do banco de dados;
        List<Usuario> usuarios = userRepository.findAll();

        return usuarioMapper.toDtoList(usuarios);
    }

    //Atualizar um usuário;
    @Transactional
    public UserResponseDTO atualizarUsuario(UUID id, UserUpdateDTO userUpdateDTO) {
        //Verificar se o usuário do ID passado existe no BD, se não existir lançar uma exceção;
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário de ID '" + id + "' não encontrado."));

        if (userUpdateDTO.getEmail() != null && !usuario.getEmail().equals(userUpdateDTO.getEmail())) {
            if (userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent()) {
                throw new ConflictException("O email '" + userUpdateDTO.getEmail() + "' já está cadastrado por outro usuário.");
            }
        }

        usuarioMapper.updateEntityFromDto(userUpdateDTO, usuario);

        Usuario usuarioAtualizado = userRepository.save(usuario);
        return usuarioMapper.toDto(usuarioAtualizado);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        //Verificar se o usuário existe, se não existir lançar uma exceção;
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário de ID '" + id + "' não encontrado.");
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Não foi possível deletar o usuário de ID '" + id + "'. Existem registros vinculados a este usuários.");
        }
    }

    @Transactional
    public UserResponseDTO atribuirCargo(UUID id, UsuarioCargoUpdateDTO usuarioCargoUpdateDTO) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário de ID '" + id + "' não encontrado."));

        Cargo cargo = cargoRepository.findById(usuarioCargoUpdateDTO.getIdCargo())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo de ID '" + usuarioCargoUpdateDTO.getIdCargo() + "' não encontrado."));

        if (usuario.getCargo() != null && usuario.getCargo().getId().equals(cargo.getId())) {
            throw new BadRequestException("O usuário já possui o cargo '" + cargo.getNome() + "'.");
        }

        usuario.setCargo(cargo);

        Usuario usuarioAtualizado = userRepository.save(usuario);

        return usuarioMapper.toDto(usuarioAtualizado);
    }

}
