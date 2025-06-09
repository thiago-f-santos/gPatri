package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ItemPatrimonioCreateDTO {

    @NotNull(message = "O id de patrimônio não pode ser nulo")
    private UUID idPatrimonio;

    @NotNull(message = "O id de condição não pode ser nulo")
    private UUID idCondicao;

}
