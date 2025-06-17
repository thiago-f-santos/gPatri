package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemEmprestimoCreateDTO {

    @NotNull(message = "O id do Item Patrimonio não pode ser nulo ou vazio")
    private UUID idItemPatrimonio;

    @Min(value = 1, message = "A quantidade precisa ser igual ou superior a 1")
    @NotNull(message = "A quantidade não pode ser nula")
    private Integer quantidade;

}
