package br.edu.ifg.numbers.msusuarios.service;

import br.edu.ifg.numbers.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserUpdateDTO;
import br.edu.ifg.numbers.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.msusuarios.mapper.UsuarioMapper;
import br.edu.ifg.numbers.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.msusuarios.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioService(UserRepository userRepository, CargoRepository cargoRepository, UsuarioMapper usuarioMapper) {
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
        this.usuarioMapper = usuarioMapper;
    }

    //Criar um novo usuário;
    @Transactional
    public UserResponseDTO criarUsuario(UserRequestDTO userRequestDTO) {
        //Verificar se ja existe um usuário com o mesmo email;
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new ConflictException("O email '" + userRequestDTO.getEmail() + "' já está cadastrado por outro usuário.");
        }

        Cargo cargo = cargoRepository.findById(userRequestDTO.getIdCargo())
                .orElseThrow(() -> new BadRequestException("Cargo de ID '" + userRequestDTO.getIdCargo() + "' não foi encontrado."));

        Usuario usuario = usuarioMapper.toEntity(userRequestDTO, cargo);

        Usuario novoUsuario = userRepository.save(usuario);

        return usuarioMapper.toDto(novoUsuario);
    }

    //Buscar um usuário pelo ID;
    public UserResponseDTO buscarPid(UUID id) {
        //Verificar se o usuário existe se não lançar uma exceção;
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário de ID '" + id + "' não encontrado."));
        return usuarioMapper.toDto(usuario);
    }

    //Buscar todos os usuários;
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

        Cargo cargo = usuario.getCargo();
        if(userUpdateDTO.getIdCargo() != null && !usuario.getCargo().getId().equals(userUpdateDTO.getIdCargo())) {
            cargo = cargoRepository.findById(userUpdateDTO.getIdCargo())
                    .orElseThrow(() -> new BadRequestException("Cargo de ID '" + userUpdateDTO.getIdCargo() + "' não foi encontrado."));
        }

        usuarioMapper.updateEntityFromDto(userUpdateDTO, usuario, cargo);

        Usuario usuarioAtualizado = userRepository.save(usuario);
        return usuarioMapper.toDto(usuarioAtualizado);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        //Verificar se o usuário existe, se não existir lançar uma exceção;
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário de ID '" + id + "' não encontrado.");
        }
        //Deletar o usuário do banco de dados;
        userRepository.deleteById(id);
    }
}
