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
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "idUsuarioAvaliador", ignore = true)
    @Mapping(target = "situacao", ignore = true)
    @Mapping(target = "itensEmprestimo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Emprestimo createDtoToEmprestimo(EmprestimoCreateDTO emprestimoCreateDTO);

    @Mapping(target = "situacao", source = "situacao")
    @Mapping(target = "itensEmprestimo", source = "itensEmprestimo")
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "usuarioAvaliador", ignore = true)
    EmprestimoResponseDTO emprestimoToEmprestimoResponseDto(Emprestimo emprestimo);
    
}
