package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCargoUpdateDTO {

    @NotNull(message = "O ID do cargo é obrigatório")
    private UUID idCargo;
}
