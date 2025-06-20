package br.edu.ifg.numbers.gpatri.msusuarios.service;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.CargoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;
    private final CargoMapper cargoMapper;

    @Autowired
    public CargoService(CargoRepository cargoRepository, CargoMapper cargoMapper) {
        this.cargoRepository = cargoRepository;
        this.cargoMapper = cargoMapper;
    }

    @Transactional
    public CargoResponseDTO criarCargo(CargoRequestDTO cargoRequestDTO) {
        // Verifica se já existe um cargo com o mesmo nome
        if(cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException("Já existe um cargo com o nome: " + cargoRequestDTO.getNome());
        }

        Cargo cargo = cargoMapper.toEntity(cargoRequestDTO);

        Cargo cargoSalvo = cargoRepository.save(cargo);

        return cargoMapper.toDto(cargoSalvo);
    }

    // Buscar um cargo pelo ID;
    public CargoResponseDTO buscarPorId(UUID id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo de ID '" + id + "' não encontrado."));
        return cargoMapper.toDto(cargo);
    }

    // Buscar cargo pelo nome;
    public CargoResponseDTO buscarPorNome(String nome) {
        Cargo cargo = cargoRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo de nome '" + nome + "' não encontrado."));
        return cargoMapper.toDto(cargo);
    }

    // Buscar todos os cargos;
    public List<CargoResponseDTO> buscarTodos() {
        List<Cargo> cargos = cargoRepository.findAll();
        return cargoMapper.toDtoList(cargos);
    }

    // Atualizar um cargo pelo ID;
    @Transactional
    public CargoResponseDTO atualizarCargo(UUID id, CargoRequestDTO cargoRequestDTO) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo de ID '" + id + "' não encontrado."));

        if (!cargo.getNome().equals(cargoRequestDTO.getNome()) && cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException("Já existe um cargo com o nome: " + cargoRequestDTO.getNome());
        }

        cargoMapper.updateEntityFromDto(cargoRequestDTO, cargo);
        Cargo cargoAtualizado = cargoRepository.save(cargo);
        return cargoMapper.toDto(cargoAtualizado);
    }

    // Deletar um cargo pelo ID;
    @Transactional
    public void deletarCargo(UUID id) {
        if (!cargoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cargo de ID '" + id + "' não encontrado.");
        }
        try {
            cargoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Não é possivel deletar o cargo de ID '" + id + "' pois ele está vinculado a um ou mais usuários. Antes de deletar, remova os usuários vinculados a este cargo.");
        }
    }
}
