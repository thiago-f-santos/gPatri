package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemPatrimonioMapper {

    ItemPatrimonioResponseDTO itemPatrimonioToPatrimonioResponseDto(ItemPatrimonio itemPatrimonio);

}
