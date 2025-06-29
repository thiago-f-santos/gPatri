package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargoRequestDTO {

    @NotBlank(message = "O nome do cargo é obrigatório")
    @Size(min = 3, max = 50, message = "O nome do cargo deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotEmpty(message = "As permissões são obrigatórias")
    private Set<String> permissoes;

}
