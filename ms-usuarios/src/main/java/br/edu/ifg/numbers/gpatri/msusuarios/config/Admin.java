package br.edu.ifg.numbers.gpatri.msusuarios.config;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.enums.PermissaoEnum;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class Admin implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Admin.class);

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public Admin(UserRepository userRepository, CargoRepository cargoRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        cargoAdmin("ADMIN");

        Optional<Cargo> admin = cargoRepository.findByNome("ADMIN");

        if (admin.isPresent()) {
            Cargo cargoAdmin = admin.get();

            Optional<Usuario> adminUser = userRepository.findByEmail("admin@gmail.com");

            if (adminUser.isEmpty()) {
                Usuario adminUsuario = new Usuario();
                adminUsuario.setNome("Administrador");
                adminUsuario.setSobrenome("Geral");
                adminUsuario.setEmail("admin@gmail.com");
                adminUsuario.setSenha(passwordEncoder.encode("senha123"));
                adminUsuario.setCargo(cargoAdmin);

                userRepository.save(adminUsuario);

                logger.info("ADMIN criado com sucesso");
                logger.info("Email: admin@gmail.com");
                logger.info("Senha: senha123");
            } else {
                logger.info("Usuário ADMIN já existe.");
            }
        } else {
            logger.error("Cargo ADMIN não encontrado. Certifique-se de que o cargo ADMIN foi criado antes de iniciar a aplicação.");
            logger.error("O ADMIN não foi criado");
        }
    }

    private void cargoAdmin(String nomeCargo) {
        if (cargoRepository.findByNome(nomeCargo).isEmpty()) {
            Cargo cargo = new Cargo(nomeCargo);
            Set<PermissaoEnum> permissoes = new HashSet<>(Arrays.asList(PermissaoEnum.values()));
            cargo.setPermissoes(permissoes);
            cargoRepository.save(cargo);
        } else {
            logger.info("Cargo admin já existe: ", nomeCargo);
        }
    }
}
