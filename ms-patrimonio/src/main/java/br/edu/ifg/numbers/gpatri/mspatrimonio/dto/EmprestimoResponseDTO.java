package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class EmprestimoResponseDTO {

    private UUID id;

    private UUID idUsuario;

    private boolean aprovado;

    private List<ItemEmprestimoResponseDTO> itensEmprestimo;

    private Date dataEmprestimo;

    private Date dataDevolucao;

}
