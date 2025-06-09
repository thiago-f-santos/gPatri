package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Condicao;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoResponseDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.CondicaoUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CondicaoMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Condicao createDtoToCondicao(CondicaoCreateDTO condicaoCreateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Condicao updateDtoToCondicao(CondicaoUpdateDTO condicaoUpdateDTO);

    Condicao responseDtoToCondicao(CondicaoResponseDTO condicaoResponseDTO);

    CondicaoResponseDTO condicaoToCondicaoResponseDto(Condicao condicao);

}
