package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemEmprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemEmprestimoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ItemPatrimonioMapper.class}
)
public interface ItemEmprestimoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emprestimo", ignore = true)
    @Mapping(target = "itemPatrimonio", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ItemEmprestimo createDtoToItemEmprestimo(ItemEmprestimoCreateDTO itemEmprestimoCreateDTO);

    @Mapping(target = "itemPatrimonio", source = "itemPatrimonio")
    ItemEmprestimoResponseDTO itemEmprestimoToItemEmprestimoResponseDTO(ItemEmprestimo itemEmprestimo);

}
