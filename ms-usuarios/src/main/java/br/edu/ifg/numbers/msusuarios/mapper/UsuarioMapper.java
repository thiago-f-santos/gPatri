package br.edu.ifg.numbers.msusuarios.mapper;

import br.edu.ifg.numbers.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.msusuarios.dto.UserRequestDTO;
import br.edu.ifg.numbers.msusuarios.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

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
    @Mapping(target = "cargo", source = "cargoObj")
    @Mapping(target = "id", ignore = true)
    Usuario toEntity(UserRequestDTO dto, Cargo cargoObj);

    List<UserResponseDTO> toDtoList(List<Usuario> usuarios);

    @Mapping(target = "nome", source = "dto.nome", ignore = true)
    @Mapping(target = "sobrenome", source = "dto.sobrenome", ignore = true)
    @Mapping(target = "email", source = "dto.email", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cargo", source = "cargoObj")
    @Mapping(target = "senha", source = "dto.senha", ignore = true)
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget Usuario usuario, Cargo cargoObj);

}
