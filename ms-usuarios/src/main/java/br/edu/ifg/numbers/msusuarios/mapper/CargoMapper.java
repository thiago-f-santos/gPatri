package br.edu.ifg.numbers.msusuarios.mapper;

import br.edu.ifg.numbers.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.CargoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CargoMapper {

    @Mapping(target = "id", source = "cargo.id")
    @Mapping(target = "nome", source = "cargo.nome")
    CargoResponseDTO toDto(Cargo cargo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "dto.nome")
    Cargo toEntity(CargoRequestDTO dto);

    List<CargoResponseDTO> toDtoList(List<Cargo> cargos);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CargoRequestDTO dto, @MappingTarget Cargo cargo);
}
