package br.edu.ifg.numbers.msusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotBlank(message = "O sobrenome é obrigatório")
    @Size(min = 3, max = 50, message = "O sobrenome deve ter entre 3 e 50 caracteres")
    private String sobrenome;

    @NotBlank(message = "O email é obrigatório")
    @Size(min = 5, max = 500, message = "O email deve ter entre 5 e 100 caracteres")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @NotBlank(message = "O cargo é obrigatório")
    @Size(min = 3, max = 100, message = "O cargo deve ter entre 3 e 50 caracteres")
    private String cargo;
}
