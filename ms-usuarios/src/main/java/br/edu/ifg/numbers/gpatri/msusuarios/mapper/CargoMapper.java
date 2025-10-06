package br.edu.ifg.numbers.gpatri.msusuarios.mapper;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.CargoResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CargoMapper {

    @Mapping(target = "id", source = "cargo.id")
    @Mapping(target = "nome", source = "cargo.nome")
    @Mapping(target = "permissoes", source = "cargo.permissoes")
    CargoResponseDTO toDto(Cargo cargo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "dto.nome")
    Cargo toEntity(CargoRequestDTO dto);

    List<CargoResponseDTO> toDtoList(List<Cargo> cargos);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CargoRequestDTO dto, @MappingTarget Cargo cargo);
    default Set<String> convertPermissoesToString(Set<PermissaoEnum> permissoes) {
        if (permissoes == null) {
            return Collections.emptySet();
        }
        return permissoes.stream()
                .map(PermissaoEnum::name)
                .collect(Collectors.toSet());
    }

    default Set<PermissaoEnum> convertStringToPermissoes(Set<String> permissoes) {
        if (permissoes == null) {
            return new HashSet<>();
        }
        return permissoes.stream()
                .map(permissao -> {
                    try {
                        return PermissaoEnum.valueOf(permissao.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new BadRequestException(String.format("Permissão inválida: \"%s\"\nPor favor, use nomes de permissões válidos.", permissao), e);
                    }
                })
                .collect(Collectors.toSet());
    }
  
}
