package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Patrimonio;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.PatrimonioResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatrimonioMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Patrimonio createDtoToPatrimonio(PatrimonioCreateDTO patrimonioCreateDTO);

    Patrimonio responseDtoToPatrimonio(PatrimonioResponseDTO patrimonioResponseDTO);

    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    PatrimonioResponseDTO  patrimonioToPatrimonioResponseDto(Patrimonio patrimonio);

}
