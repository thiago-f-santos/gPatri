package br.edu.ifg.numbers.gpatri.mspatrimonio.exception.handler;

import br.edu.ifg.numbers.gpatri.mspatrimonio.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityNotFoundException(EntityNotFoundException ex, HttpServletRequest request) {
        log.error("Api error - ", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Jakarta Validation - ", ex);
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Fields",
                        ex.getBindingResult()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> runtimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("Unexpected error - ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessage> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.error("Method not allowed error - ", ex);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorMessage(request, HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage()));
    }

    @ExceptionHandler(EmprestimoVazioException.class)
    public ResponseEntity<ErrorMessage> emprestimoVazioException(EmprestimoVazioException ex, HttpServletRequest request) {
        log.error("Emprestimo Vazio - ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(ItemEmUsoException.class)
    public ResponseEntity<ErrorMessage> itemEmUsoException(ItemEmUsoException ex, HttpServletRequest request) {
        log.error("Item em Uso - ", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(ItemEmprestimoJaExistenteException.class)
    public ResponseEntity<ErrorMessage> itemEmprestimoJaExistenteException(ItemEmprestimoJaExistenteException ex, HttpServletRequest request) {
        log.error("Item Emprestimo JaExistente - ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(QuantidadeItemIndisponivelException.class)
    public ResponseEntity<ErrorMessage> quantidadeitemIndisponivelException(QuantidadeItemIndisponivelException ex, HttpServletRequest request) {
        log.error("Quantidade item indisponivel - ", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ErrorMessage(request, HttpStatus.NOT_ACCEPTABLE, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorMessage> illegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.error("Illegal State - ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    @ExceptionHandler(SituacaoEmprestimoInvalidaException.class)
    public ResponseEntity<ErrorMessage> situacaoEmprestimoInvalidaException(SituacaoEmprestimoInvalidaException ex, HttpServletRequest request) {
        log.error("Situacao Emprestimo invalida - ", ex);
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorMessage> handleAuthorizationDeniedException(Exception ex, HttpServletRequest request) {
        log.error("Acesso negado: ", ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Acesso negado. Você não tem permissão para acessar este recurso."));
    }

}