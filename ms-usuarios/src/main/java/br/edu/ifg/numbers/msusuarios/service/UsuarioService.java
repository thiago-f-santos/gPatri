package br.edu.ifg.numbers.msusuarios.service;

import br.edu.ifg.numbers.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserResponseDTO;
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

    @Autowired
    public UsuarioService(UserRepository userRepository, CargoRepository cargoRepository) {
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
    }

    //Criar um novo usuário;
    @Transactional
    public UserResponseDTO criarUsuario(UserRequestDTO userRequestDTO) {
        //Verificar se ja existe um usuário com o mesmo email;
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Usuário com o email já cadastrado: " + userRequestDTO.getEmail());
        }

        // Verificar se o cargo existe;
        Cargo cargo = cargoRepository.findByNome(userRequestDTO.getCargo())
                .orElseThrow(() -> new IllegalArgumentException("O cargo de nome '" + userRequestDTO.getCargo() + "' não foi encontrado."));

        Usuario usuario = new Usuario();
        usuario.setNome(userRequestDTO.getNome());
        usuario.setSobrenome(userRequestDTO.getSobrenome());
        usuario.setEmail(userRequestDTO.getEmail());
        // Depois tem que criptografar a senha antes de salvar no banco de dados. Temporariamente ta assim.
        usuario.setSenha(userRequestDTO.getSenha());
        usuario.setCargo(cargo);

        //Salvar o usuário no banco de dados;
        Usuario novoUsuario = userRepository.save(usuario);

        //Converter o usuário salvo para um UserResponseDTO e retornar;
        return responseDTO(novoUsuario);
    }

    //Buscar um usuário pelo ID;
    public UserResponseDTO buscarPid(UUID id) {
        //Verificar se o usuário existe se não lançar uma exceção;
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário de ID '" + id + "' não encontrado."));
        return responseDTO(usuario);
    }

    //Buscar todos os usuários;
    public List<UserResponseDTO> buscarTodos() {
        //Buscar todos os usuários do banco de dados;
        List<Usuario> usuarios = userRepository.findAll();

        //Converter a lista de usuários para uma lista de UserResponseDTO e retornar;
        return usuarios.stream()
                .map(this::responseDTO)
                .collect(Collectors.toList());
    }

    //Atualizar um usuário;
    @Transactional
    public UserResponseDTO atualizarUsuario(UUID id, UserRequestDTO userRequestDTO) {
        //Verificar se o usuário do ID passado existe no BD, se não existir lançar uma exceção;
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário de ID '" + id + "' não encontrado."));

        //Verificar se o email foi alterado e se o novo já está cadastrado por outro usuário, se sim lançar uma exceção;
        if (!usuario.getEmail().equals(userRequestDTO.getEmail()) && userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("O email '" + userRequestDTO.getEmail() + "' já está cadastrado por outro usuário.");
        }

        //Buscar o cargo pelo nome, se não existir lançar uma exceção (Caso o cargo seja alterado);
        //So buscar o cargo se o nome do cargo no DTO for diferente do cargo atual do usuário, se não, manter o cargo atual;
        Cargo novoCargo = usuario.getCargo(); //cargo atual
        if (!usuario.getCargo().getNome().equals(userRequestDTO.getCargo())) {
            novoCargo = cargoRepository.findByNome(userRequestDTO.getCargo())
                    .orElseThrow(() -> new IllegalArgumentException("O cargo de nome '" + userRequestDTO.getCargo() + "' não foi encontrado."));
        }

        //Atualizar os dados do usuário com os dados do DTO;
        usuario.setNome(userRequestDTO.getNome());
        usuario.setSobrenome(userRequestDTO.getSobrenome());
        usuario.setEmail(userRequestDTO.getEmail());
        // Depois tem que criptografar a senha antes de salvar no banco de dados. Temporariamente ta assim.
        usuario.setSenha(userRequestDTO.getSenha());
        usuario.setCargo(novoCargo); //Atribuindo o novo cargo ao usuário

        //Salvar o usuário atualizado no banco de dados;
        Usuario usuarioAtualizado = userRepository.save(usuario);

        //Converter o usuário atualizado para um UserResponseDTO e retornar;
        return responseDTO(usuarioAtualizado);
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        //Verificar se o usuário existe, se não existir lançar uma exceção;
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário de ID '" + id + "' não encontrado.");
        }
        //Deletar o usuário do banco de dados;
        userRepository.deleteById(id);
    }

    private UserResponseDTO responseDTO(Usuario usuario) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(usuario.getId());
        responseDTO.setNome(usuario.getNome());
        responseDTO.setSobrenome(usuario.getSobrenome());
        responseDTO.setEmail(usuario.getEmail());
        if (usuario.getCargo() != null) {
            responseDTO.setCargo(usuario.getCargo().getNome());
        }
        return responseDTO;
    }
}
