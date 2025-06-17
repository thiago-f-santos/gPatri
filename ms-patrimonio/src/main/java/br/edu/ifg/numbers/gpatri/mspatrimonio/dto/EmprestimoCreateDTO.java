package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class EmprestimoCreateDTO {

    private UUID idUsuario;

    private List<ItemEmprestimoCreateDTO> itensEmprestimo;

    private Date dataEmprestimo;

    private Date dataDevolucao;

}
