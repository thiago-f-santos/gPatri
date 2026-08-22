package br.edu.ifg.numbers.gpatri.msusuarios.exception.handler;

import br.edu.ifg.numbers.gpatri.msusuarios.exception.BadRequestException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ConflictException;
import br.edu.ifg.numbers.gpatri.msusuarios.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/v1/recurso");
        when(request.getMethod()).thenReturn("DELETE");
    }

    @Test
    @DisplayName("Deve retornar status 409 Conflict ao capturar DataIntegrityViolationException")
    void deveRetornarConflictAoCapturarDataIntegrityViolationException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleDataIntegrityViolationException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getStatusMessage());
        assertEquals("/api/v1/recurso", response.getBody().getPath());
        assertEquals("DELETE", response.getBody().getMethod());
        assertEquals("Violação de integridade referencial: não é possível excluir ou alterar o recurso pois ele está vinculado a outros registros.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar status 404 Not Found ao capturar ResourceNotFoundException")
    void deveRetornarNotFoundAoCapturarResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Recurso não encontrado");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleResourceNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recurso não encontrado", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar status 400 Bad Request ao capturar BadRequestException")
    void deveRetornarBadRequestAoCapturarBadRequestException() {
        BadRequestException exception = new BadRequestException("Requisição inválida");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleBadRequestException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Requisição inválida", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar status 409 Conflict ao capturar ConflictException")
    void deveRetornarConflictAoCapturarConflictException() {
        ConflictException exception = new ConflictException("Conflito de dados");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleConflictException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflito de dados", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar status 401 Unauthorized ao capturar BadCredentialsException")
    void deveRetornarUnauthorizedAoCapturarBadCredentialsException() {
        BadCredentialsException exception = new BadCredentialsException("Credenciais inválidas");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleBadCredentialsException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email ou senha incorretos.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar status 403 Forbidden ao capturar AccessDeniedException")
    void deveRetornarForbiddenAoCapturarAccessDeniedException() {
        AccessDeniedException exception = new AccessDeniedException("Acesso negado");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleAuthorizationDeniedException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado. Você não tem permissão para acessar este recurso.", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Deve retornar status 500 Internal Server Error ao capturar Exception genérica")
    void deveRetornarInternalServerErrorAoCapturarExceptionGenerica() {
        Exception exception = new RuntimeException("Erro inesperado");

        ResponseEntity<ErrorMessage> response = globalExceptionHandler.handleGlobalException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.", response.getBody().getMessage());
    }
}
