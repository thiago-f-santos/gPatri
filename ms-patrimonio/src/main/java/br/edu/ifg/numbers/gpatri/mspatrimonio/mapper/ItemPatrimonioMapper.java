package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.ItemPatrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.ItemPatrimonioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemPatrimonioMapper {

    ItemPatrimonioResponseDTO itemPatrimonioToPatrimonioResponseDto(ItemPatrimonio itemPatrimonio);

}
