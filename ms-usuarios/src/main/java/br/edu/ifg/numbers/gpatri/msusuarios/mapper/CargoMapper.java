package br.edu.ifg.numbers.gpatri.msusuarios.mapper;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {PermissaoMapper.class})
public interface CargoMapper {

    @Mapping(target = "id", source = "cargo.id")
    @Mapping(target = "nome", source = "cargo.nome")
    @Mapping(target = "permissoes", source = "cargo.permissoes")
    CargoResponseDTO toDto(Cargo cargo);

    List<CargoResponseDTO> toDtoList(List<Cargo> cargos);
}
