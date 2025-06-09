package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CondicaoUpdateDTO {

    @NotBlank(message = "A condição não pode estar vazia")
    private String condicao;

}
