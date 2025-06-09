package br.edu.ifg.numbers.gpatri.mspatrimonio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CondicaoCreateDTO {

    @NotBlank(message = "A condição não pode estar vazia")
    private String condicao;

}
