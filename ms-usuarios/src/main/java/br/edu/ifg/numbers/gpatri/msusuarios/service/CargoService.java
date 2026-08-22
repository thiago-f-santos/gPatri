package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.CargoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;
    private final PermissaoRepository permissaoRepository;
    private final CargoMapper cargoMapper;

    @Transactional
    public CargoResponseDTO criarCargo(CargoRequestDTO cargoRequestDTO) {
        if (cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException(String.format("Já existe um cargo com o nome: %s", cargoRequestDTO.getNome()));
        }

        Set<Permissao> permissoes = buscarPermissoesValidadas(cargoRequestDTO.getPermissoesIds());

        Cargo cargo = Cargo.builder()
                .nome(cargoRequestDTO.getNome())
                .permissoes(permissoes)
                .build();

        cargo = cargoRepository.save(cargo);
        return cargoMapper.toDto(cargo);
    }

    @Transactional(readOnly = true)
    public CargoResponseDTO findById(UUID id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id)));
        return cargoMapper.toDto(cargo);
    }

    @Transactional(readOnly = true)
    public CargoResponseDTO buscarPorNome(String nome) {
        Cargo cargo = cargoRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de nome '%s' não encontrado.", nome)));
        return cargoMapper.toDto(cargo);
    }

    @Transactional(readOnly = true)
    public List<CargoResponseDTO> buscarTodos() {
        List<Cargo> cargos = cargoRepository.findAll();
        return cargoMapper.toDtoList(cargos);
    }

    @Transactional
    public CargoResponseDTO atualizarCargo(UUID id, CargoRequestDTO cargoRequestDTO) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Cargo de ID '%s' não encontrado.", id)));

        if (!cargo.getNome().equals(cargoRequestDTO.getNome()) && cargoRepository.findByNome(cargoRequestDTO.getNome()).isPresent()) {
            throw new ConflictException(String.format("Já existe um cargo com o nome: %s", cargoRequestDTO.getNome()));
        }

        Set<Permissao> novasPermissoes = buscarPermissoesValidadas(cargoRequestDTO.getPermissoesIds());

        cargo.setNome(cargoRequestDTO.getNome());
        cargo.setPermissoes(novasPermissoes);
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
            cargoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("Não é possivel deletar o cargo de ID '%s' pois ele está " +
                    "vinculado a um ou mais usuários. Antes de deletar, remova os usuários vinculados a este cargo.", id));
        }
    }

    private Set<Permissao> buscarPermissoesValidadas(Set<UUID> permissoesIds) {
        if (permissoesIds == null || permissoesIds.isEmpty()) {
            throw new BadRequestException("Pelo menos uma permissão deve ser informada para o cargo.");
        }

        List<Permissao> permissoesEncontradas = permissaoRepository.findAllById(permissoesIds);

        if (permissoesEncontradas.size() != permissoesIds.size()) {
            Set<UUID> idsEncontrados = permissoesEncontradas.stream().map(Permissao::getId).collect(Collectors.toSet());
            Set<UUID> idsFaltantes = permissoesIds.stream()
                    .filter(id -> !idsEncontrados.contains(id))
                    .collect(Collectors.toSet());

            throw new BadRequestException(String.format("As seguintes permissões não foram encontradas no sistema: %s", idsFaltantes));
        }

        return new HashSet<>(permissoesEncontradas);
    }
}
