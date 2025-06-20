package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class EmprestimoUpdateDTO {

    private Date dataEmprestimo;

    private Date dataDevolucao;

}
