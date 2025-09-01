package br.edu.ifg.numbers.gpatri.msusuarios.config;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitialSetupConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        cargoAdmin();
        cargoUsuario();

        Optional<Cargo> admin = cargoRepository.findByNome("Administrador");

        if (admin.isPresent()) {
            Cargo cargoAdmin = admin.get();

            Optional<Usuario> adminUser = userRepository.findByEmail("admin@admin.com");

            if (adminUser.isEmpty()) {
                Usuario adminUsuario = new Usuario();
                adminUsuario.setNome("Administrador");
                adminUsuario.setSobrenome("Geral");
                adminUsuario.setEmail("admin@admin.com");
                adminUsuario.setSenha(passwordEncoder.encode("admin123"));
                adminUsuario.setCargo(cargoAdmin);

                userRepository.save(adminUsuario);

                log.info("ADMIN criado com sucesso");
                log.info("Email: admin@admin.com");
                log.info("Senha: admin123");
            } else {
                log.info("Usuário ADMIN já existe.");
            }
        } else {
            log.error("Cargo ADMIN não encontrado. Certifique-se de que o cargo ADMIN foi criado antes de iniciar a aplicação.");
            log.error("O ADMIN não foi criado");
        }
    }

    private void cargoAdmin() {
        if (cargoRepository.findByNome("Administrador").isEmpty()) {
            Cargo cargo = new Cargo("Administrador");
            Set<PermissaoEnum> permissoes = new HashSet<>(Arrays.asList(PermissaoEnum.values()));
            cargo.setPermissoes(permissoes);
            cargoRepository.save(cargo);
        } else {
            log.info("Cargo admin já existe.");
        }
    }

    private void cargoUsuario() {
        if (cargoRepository.findByNome("Usuário").isEmpty()) {
            Cargo cargo = new Cargo("Usuário");
            Set<PermissaoEnum> permissoes = new HashSet<>(
                    Arrays.asList(PermissaoEnum.USUARIO_EDITAR, PermissaoEnum.EMPRESTIMO_EDITAR, PermissaoEnum.EMPRESTIMO_EXCLUIR,
                            PermissaoEnum.EMPRESTIMO_LISTAR, PermissaoEnum.EMPRESTIMO_SOLICITAR, PermissaoEnum.PATRIMONIO_LISTAR,
                            PermissaoEnum.ITEM_PATRIMONIO_LISTAR, PermissaoEnum.CATEGORIA_LISTAR, PermissaoEnum.USUARIO_LISTAR
                    ));
            cargo.setPermissoes(permissoes);
            cargoRepository.save(cargo);
        } else {
            log.info("Cargo usuário já existe.");
        }
    }

}
