package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import br.edu.ifg.numbers.gpatri.msusuarios.mapper.PermissaoMapper;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;
    private final PermissaoMapper permissaoMapper;

    @Autowired
    public PermissaoService(PermissaoRepository permissaoRepository, PermissaoMapper permissaoMapper) {
        this.permissaoRepository = permissaoRepository;
        this.permissaoMapper = permissaoMapper;
    }

    public List<PermissaoResponseDTO> buscarTodas() {
        List<Permissao> permissoes = permissaoRepository.findAllByOrderByCategoriaAscNomeAsc();
        return permissaoMapper.toDtoList(permissoes);
    }

    public PermissaoResponseDTO findById(UUID id) {
        Permissao permissao = permissaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Permissão de ID '%s' não encontrada.", id)));
        return permissaoMapper.toDto(permissao);
    }

    public List<PermissaoResponseDTO> buscarPorCategoria(String categoria) {
        List<Permissao> permissoes = permissaoRepository.findByCategoriaOrderByNomeAsc(categoria);
        return permissaoMapper.toDtoList(permissoes);
    }
}
