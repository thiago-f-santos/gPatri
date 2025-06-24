package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @Size(min = 3, max = 50, message = "O sobrenome deve ter entre 3 e 50 caracteres")
    private String sobrenome;

    @Size(min = 5, max = 500, message = "O email deve ter entre 5 e 100 caracteres")
    private String email;

}
