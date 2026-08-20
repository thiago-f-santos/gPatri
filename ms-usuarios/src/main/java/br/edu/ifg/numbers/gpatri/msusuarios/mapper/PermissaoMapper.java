package br.edu.ifg.numbers.gpatri.msusuarios.mapper;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.PermissaoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PermissaoMapper {
    PermissaoResponseDTO toDto(Permissao permissao);
    List<PermissaoResponseDTO> toDtoList(List<Permissao> permissoes);
    Set<PermissaoResponseDTO> toDtoSet(Set<Permissao> permissoes);
}
