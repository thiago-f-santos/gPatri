package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter @Setter
public class EmprestimoCreateDTO {

    @NotEmpty(message = "Para criar um empréstimo, é preciso pedir ao menos um item.")
    private List<ItemEmprestimoCreateDTO> itensEmprestimo;

    @NotNull(message = "A data do empréstimo não pode ser nula")
    private Date dataEmprestimo;

    @NotNull(message = "A data de devolução não pode ser nula")
    private Date dataDevolucao;

}
