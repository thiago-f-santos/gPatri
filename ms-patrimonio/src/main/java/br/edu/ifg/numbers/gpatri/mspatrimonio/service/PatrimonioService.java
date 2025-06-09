package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Categoria;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.CategoriaMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.PatrimonioMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.PatrimonioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatrimonioService {

    private final PatrimonioRepository patrimonioRepository;
    private final PatrimonioMapper patrimonioMapper;

    private final CategoriaMapper categoriaMapper;
    private final CategoriaService categoriaService;

    @Transactional
    public PatrimonioResponseDTO save(PatrimonioCreateDTO patrimonioCreateDTO) {
        Patrimonio patrimonio = patrimonioMapper.createDtoToPatrimonio(patrimonioCreateDTO);
        Categoria categoria = categoriaMapper.responseDtoToCategoria(categoriaService.findById(patrimonioCreateDTO.getIdCategoria()));
        patrimonio.setCategoria(categoria);
        patrimonio.setCreatedAt(Instant.now());
        patrimonio = patrimonioRepository.save(patrimonio);
        return patrimonioMapper.patrimonioToPatrimonioResponseDto(patrimonio);
    }

    @Transactional
    public PatrimonioResponseDTO update(UUID id, PatrimonioUpdateDTO patrimonioUpdateDTO) {
        Patrimonio patrimonio = patrimonioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Patrimonio não encontrado"));
        if (patrimonioUpdateDTO.getIdCategoria() != null) {
            Categoria categoria = categoriaMapper.responseDtoToCategoria(categoriaService.findById(patrimonioUpdateDTO.getIdCategoria()));
            patrimonio.setCategoria(categoria);
        }
        if (patrimonioUpdateDTO.getNome() != null) {
            patrimonio.setNome(patrimonioUpdateDTO.getNome());
        }
        if (patrimonioUpdateDTO.getDescricao() != null) {
            patrimonio.setDescricao(patrimonioUpdateDTO.getDescricao());
        }
        if (patrimonioUpdateDTO.getPrecoEstimado() != null) {
            patrimonio.setPrecoEstimado(patrimonioUpdateDTO.getPrecoEstimado());
        }
        patrimonio.setUpdatedAt(Instant.now());
        patrimonio = patrimonioRepository.save(patrimonio);
        return patrimonioMapper.patrimonioToPatrimonioResponseDto(patrimonio);
    }

    @Transactional
    public void deleteById(UUID id) {
        if(!patrimonioRepository.existsById(id)) {
            throw new EntityNotFoundException("Patrimonio não encontrado");
        }
        patrimonioRepository.deleteById(id);
    }

    public PatrimonioResponseDTO findById(UUID id) {
        Patrimonio patrimonio = patrimonioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Patrimonio não encontrado"));
        return patrimonioMapper.patrimonioToPatrimonioResponseDto(patrimonio);
    }

    public List<PatrimonioResponseDTO> findAll() {
        List<Patrimonio> patrimonios = patrimonioRepository.findAll();
        return patrimonios.stream().map(patrimonioMapper::patrimonioToPatrimonioResponseDto).collect(Collectors.toList());
    }

}
