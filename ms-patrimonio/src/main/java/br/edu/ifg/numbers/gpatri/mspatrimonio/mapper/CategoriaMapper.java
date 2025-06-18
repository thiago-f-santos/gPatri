package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Categoria;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CategoriaResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    Categoria createDtoToCategoria(CategoriaCreateDTO categoriaCreateDTO);

    Categoria responseDtoToCategoria(CategoriaResponseDTO categoriaResponseDTO);

    @Mapping(target = "idCategoriaMae", source = "categoriaMae.id")
    @Mapping(target = "categoriaMaeNome", source = "categoriaMae.nome")
    CategoriaResponseDTO categoriaToCategoriaResponseDTO(Categoria categoria);

}
