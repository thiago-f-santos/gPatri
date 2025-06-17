package br.edu.ifg.numbers.gpatri.mspatrimonio.mapper;

import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.Emprestimo;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoCreateDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.dto.EmprestimoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ItemEmprestimoMapper.class, ItemPatrimonioMapper.class}
)
public interface EmprestimoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itensEmprestimo", source = "itensEmprestimo")
    Emprestimo createDtoToEmprestimo(EmprestimoCreateDTO emprestimoCreateDTO);

    @Mapping(target = "itensEmprestimo", source = "itensEmprestimo")
    EmprestimoResponseDTO emprestimoToEmprestimoResponseDto(Emprestimo emprestimo);

}
