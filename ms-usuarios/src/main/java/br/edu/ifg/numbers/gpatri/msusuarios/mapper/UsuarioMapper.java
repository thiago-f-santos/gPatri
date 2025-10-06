package br.edu.ifg.numbers.gpatri.msusuarios.mapper;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.UserUpdateDTO;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "sobrenome", source = "sobrenome")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "idCargo", source = "cargo.id")
    @Mapping(target = "cargo", source = "cargo.nome")
    UserResponseDTO toDto(Usuario usuario);

    @Mapping(target = "nome", source = "dto.nome")
    @Mapping(target = "sobrenome", source = "dto.sobrenome")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "id", ignore = true)
    Usuario toEntity(UserRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "nome", source = "dto.nome")
    void updateEntityFromDto(UserUpdateDTO dto, @MappingTarget Usuario usuario);

}
