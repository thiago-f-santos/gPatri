package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import br.edu.ifg.numbers.gpatri.mspatrimonio.client.dto.ExternalUserDTO;
import br.edu.ifg.numbers.gpatri.mspatrimonio.domain.enums.SituacaoEmprestimo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class EmprestimoResponseDTO {

    private UUID id;

    private ExternalUserDTO usuario;

    private ExternalUserDTO usuarioAvaliador;

    private SituacaoEmprestimo situacao;

    private List<ItemEmprestimoResponseDTO> itensEmprestimo;

    private Date dataEmprestimo;

    private Date dataDevolucao;

}
