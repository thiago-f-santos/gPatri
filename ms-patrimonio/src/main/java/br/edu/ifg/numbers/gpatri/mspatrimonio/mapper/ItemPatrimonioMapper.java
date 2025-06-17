package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemPatrimonioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patrimonio", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ItemPatrimonio createDtoToItemPatrimonio(ItemPatrimonioCreateDTO itemPatrimonioCreateDTO);

    ItemPatrimonioResponseDTO itemPatrimonioToPatrimonioResponseDto(ItemPatrimonio itemPatrimonio);

}
