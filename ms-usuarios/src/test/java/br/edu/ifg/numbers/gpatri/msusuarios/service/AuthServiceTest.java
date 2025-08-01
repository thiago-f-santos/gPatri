package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.LoginRequestDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.dto.LoginResponseDTO;
import br.edu.ifg.numbers.gpatri.msusuarios.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDTO loginRequestDTO;
    private Authentication authentication;
    private String mockToken;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO("teste@gmail.com", "senha123");
        mockToken = "mockToken";

        List<GrantedAuthority> authorities = Set.of(
                        PermissaoEnum.USUARIO_LISTAR,
                        PermissaoEnum.CARGO_LISTAR
        ).stream().map(permissao -> new SimpleGrantedAuthority(permissao.name()))
         .collect(Collectors.toList());

        User userDetailsMock = new User(loginRequestDTO.getEmail(), loginRequestDTO.getSenha(), authorities);

        authentication = new UsernamePasswordAuthenticationToken(
                userDetailsMock,
                loginRequestDTO.getSenha(),
                authorities
        );
    }

    @Test
    @DisplayName("Deve autenticar o usuário e retornar um token JWT")
    void autenticarUsuario() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(mockToken);

        LoginResponseDTO response = authService.autenticarUsuario(loginRequestDTO);

        assertNotNull(response);
        assertEquals(mockToken, response.getToken());
        assertEquals(authentication.getName(), response.getUsername());
        assertFalse(response.getPermissoes().isEmpty());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando as credenciais forem inválidas")
    void autenticarUsuarioCredenciaisInvalidas() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        assertThrows(BadCredentialsException.class, () -> authService.autenticarUsuario(loginRequestDTO));

        verify(jwtTokenProvider, never()).generateToken(any());
    }
}
