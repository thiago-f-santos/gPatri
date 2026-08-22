package br.edu.ifg.numbers.gpatri.msusuarios.service;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UserDetailsServiceImp")
class UserDetailsServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImp userDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Permissao permissao = Permissao.builder()
                .id(UUID.randomUUID())
                .nome("ACESSO_ADMIN")
                .descricao("Acesso Admin")
                .categoria("ADMIN")
                .build();

        Cargo cargo = new Cargo("Administrador", Set.of(permissao));
        usuario = new Usuario(UUID.randomUUID(), "Admin", "Geral", "admin@admin.com", "senhaHash", cargo);
    }

    @Test
    @DisplayName("Deve carregar usuário por username/email com sucesso e converter permissões")
    void deveCarregarUsuarioPorEmailComSucesso() {
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@admin.com");

        assertNotNull(userDetails);
        assertEquals("admin@admin.com", userDetails.getUsername());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ACESSO_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());

        verify(userRepository, times(1)).findByEmail("admin@admin.com");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(userRepository.findByEmail("inexistente@admin.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("inexistente@admin.com"));

        verify(userRepository, times(1)).findByEmail("inexistente@admin.com");
    }
}
