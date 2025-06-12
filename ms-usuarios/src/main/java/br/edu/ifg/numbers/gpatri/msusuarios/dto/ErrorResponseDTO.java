package br.edu.ifg.numbers.gpatri.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String path;

    public ErrorResponseDTO(HttpStatus status, String mensagem, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.erro = status.getReasonPhrase();
        this.mensagem = mensagem;
        this.path = path;
    }

}
