package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemEmprestimoUpdateDTO {

    @NotBlank(message = "O id do item patrimonio não pode estar nulo ou vazio")
    private UUID idItemPatrimonio;

    @Min(value = 1, message = "A quantidade não pode ser menor que 1")
    private Integer quantidade;

}
