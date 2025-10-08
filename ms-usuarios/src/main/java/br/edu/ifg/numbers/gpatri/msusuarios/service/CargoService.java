package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
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
        if(cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException(String.format("Já existe um cargo com o nome: %s", cargoRequestDTO.getNome()));
        }

        Cargo cargo = cargoMapper.toEntity(cargoRequestDTO);
        cargo = cargoRepository.save(cargo);
        return cargoMapper.toDto(cargo);
    }

    public CargoResponseDTO findById(UUID id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id)));
        return cargoMapper.toDto(cargo);
    }

    public CargoResponseDTO buscarPorNome(String nome) {
        Cargo cargo = cargoRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de nome '%s' não encontrado.", nome)));
        return cargoMapper.toDto(cargo);
    }

    public List<CargoResponseDTO> buscarTodos() {
        List<Cargo> cargos = cargoRepository.findAll();
        return cargos.stream().map(cargoMapper::toDto).toList();
    }

    @Transactional
    public CargoResponseDTO atualizarCargo(UUID id, CargoRequestDTO cargoRequestDTO) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id)));

        if (!cargo.getNome().equals(cargoRequestDTO.getNome()) && cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException(String.format("Já existe um cargo com o nome: %s", cargoRequestDTO.getNome()));
        }

        cargoMapper.updateEntityFromDto(cargoRequestDTO, cargo);
        cargo = cargoRepository.save(cargo);

        return cargoMapper.toDto(cargo);
    }

    @Transactional
    public void deletarCargo(UUID id) {
        if (!cargoRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id));
        }
        try {
            cargoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Não é possivel deletar o cargo de ID '%s' pois ele está " +
                    "vinculado a um ou mais usuários. Antes de deletar, remova os usuários vinculados a este cargo.", id));
        }
    }
}
