package br.edu.ifg.numbers.gpatri.msusuarios.config;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para InitialSetupConfig")
class InitialSetupConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private PermissaoRepository permissaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InitialSetupConfig initialSetupConfig;

    private Permissao permissaoAdmin;
    private Permissao permissaoUser;
    private Cargo cargoAdmin;
    private Cargo cargoUser;

    @BeforeEach
    void setUp() {
        permissaoAdmin = Permissao.builder().nome("ACESSO_ADMIN").descricao("Admin").categoria("ADMIN").build();
        permissaoUser = Permissao.builder().nome("USUARIO_EDITAR").descricao("Editar usuario").categoria("USUARIOS").build();

        cargoAdmin = new Cargo("Administrador", Set.of(permissaoAdmin, permissaoUser));
        cargoUser = new Cargo("Usuário", Set.of(permissaoUser));
    }

    @Test
    @DisplayName("Deve inicializar permissões, cargos e usuário admin quando a base estiver vazia")
    void deveInicializarTudoQuandoVazio() throws Exception {
        when(permissaoRepository.count()).thenReturn(0L);
        when(permissaoRepository.findAll()).thenReturn(List.of(permissaoAdmin, permissaoUser));

        when(cargoRepository.findByNome("Administrador"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(cargoAdmin));
        when(cargoRepository.findByNome("Usuário")).thenReturn(Optional.empty());

        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdmin123");

        assertDoesNotThrow(() -> initialSetupConfig.run());

        verify(permissaoRepository, times(1)).saveAll(anyList());
        verify(cargoRepository, times(2)).save(any(Cargo.class));
        verify(userRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve pular inicialização quando dados já existem na base")
    void devePularInicializacaoQuandoJaExistem() throws Exception {
        when(permissaoRepository.count()).thenReturn(31L);
        when(cargoRepository.findByNome("Administrador")).thenReturn(Optional.of(cargoAdmin));
        when(cargoRepository.findByNome("Usuário")).thenReturn(Optional.of(cargoUser));

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setEmail("admin@admin.com");
        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(usuarioExistente));

        assertDoesNotThrow(() -> initialSetupConfig.run());

        verify(permissaoRepository, never()).saveAll(anyList());
        verify(cargoRepository, never()).save(any(Cargo.class));
        verify(userRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve tratar caso quando cargo Administrador não estiver presente")
    void deveTratarCargoAdminNaoPresente() throws Exception {
        when(permissaoRepository.count()).thenReturn(31L);
        when(cargoRepository.findByNome("Administrador")).thenReturn(Optional.empty());
        when(cargoRepository.findByNome("Usuário")).thenReturn(Optional.of(cargoUser));

        assertDoesNotThrow(() -> initialSetupConfig.run());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(Usuario.class));
    }
}
