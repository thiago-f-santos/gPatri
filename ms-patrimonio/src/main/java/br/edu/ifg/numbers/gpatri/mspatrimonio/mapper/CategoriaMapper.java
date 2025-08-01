package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Categoria;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoriaMae", ignore = true)
    @Mapping(target = "subcategorias", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Categoria createDtoToCategoria(CategoriaCreateDTO categoriaCreateDTO);

    @Mapping(target = "categoriaMae", ignore = true)
    @Mapping(target = "subcategorias", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Categoria responseDtoToCategoria(CategoriaResponseDTO categoriaResponseDTO);

    @Mapping(target = "idCategoriaMae", source = "categoriaMae.id")
    @Mapping(target = "categoriaMaeNome", source = "categoriaMae.nome")
    CategoriaResponseDTO categoriaToCategoriaResponseDTO(Categoria categoria);

}
