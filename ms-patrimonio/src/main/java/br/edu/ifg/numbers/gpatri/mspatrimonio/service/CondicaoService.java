package br.edu.ifg.numbers.gpatri.mspatrimonio.service;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Condicao;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoUpdateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.mapper.CondicaoMapper;
import br.edu.ifg.numbers.gpatri.mspatrimonio.repository.CondicaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CondicaoService {

    private final CondicaoRepository condicaoRepository;
    private final CondicaoMapper condicaoMapper;

    @Transactional
    public CondicaoResponseDTO save(CondicaoCreateDTO condicaoCreateDTO) {
        Condicao condicao = condicaoMapper.createDtoToCondicao(condicaoCreateDTO);
        condicao.setCreatedAt(Instant.now());
        condicao = condicaoRepository.save(condicao);
        return condicaoMapper.condicaoToCondicaoResponseDto(condicao);
    }

    @Transactional
    public CondicaoResponseDTO update(UUID id, CondicaoUpdateDTO condicaoUpdateDTO) {
        Condicao condicao = condicaoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Condição não encontrada"));
        if (condicaoUpdateDTO.getCondicao() != null) {
            condicao.setCondicao(condicaoUpdateDTO.getCondicao());
        }
        condicao.setUpdatedAt(Instant.now());
        condicao = condicaoRepository.save(condicao);
        return condicaoMapper.condicaoToCondicaoResponseDto(condicao);
    }

    @Transactional
    public void deleteById(UUID id) {
        if(!condicaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Condição não encontrada");
        }
        condicaoRepository.deleteById(id);
    }

    public CondicaoResponseDTO findById(UUID id) {
        Condicao condicao = condicaoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Condição não encontrada"));
        return condicaoMapper.condicaoToCondicaoResponseDto(condicao);
    }

    public List<CondicaoResponseDTO> findAll() {
        List<Condicao> condicoes = condicaoRepository.findAll();
        return condicoes.stream().map(condicaoMapper::condicaoToCondicaoResponseDto).toList();
    }

}
