package br.edu.ifg.numbers.gpatri.msusuarios.config;

import br.edu.ifg.numbers.gpatri.msusuarios.domain.Cargo;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Permissao;
import br.edu.ifg.numbers.gpatri.msusuarios.domain.Usuario;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.CargoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.PermissaoRepository;
import br.edu.ifg.numbers.gpatri.msusuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitialSetupConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final PermissaoRepository permissaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        inicializarPermissoes();
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
        }
    }

    private void inicializarPermissoes() {
        if (permissaoRepository.count() == 0) {
            log.info("Catálogo de permissões vazio. Inicializando permissões padrão...");
            List<Permissao> lista = List.of(
                    new Permissao("ACESSO_ADMIN", "Acesso administrativo geral", "ADMIN"),
                    new Permissao("USUARIO_CADASTRAR", "Cadastrar novos usuários", "USUARIOS"),
                    new Permissao("USUARIO_EDITAR", "Editar usuários", "USUARIOS"),
                    new Permissao("USUARIO_EXCLUIR", "Excluir usuários", "USUARIOS"),
                    new Permissao("USUARIO_LISTAR", "Listar usuários", "USUARIOS"),
                    new Permissao("CARGO_CADASTRAR", "Cadastrar novos cargos", "CARGOS"),
                    new Permissao("CARGO_EDITAR", "Editar cargos", "CARGOS"),
                    new Permissao("CARGO_EXCLUIR", "Excluir cargos", "CARGOS"),
                    new Permissao("CARGO_LISTAR", "Listar cargos", "CARGOS"),
                    new Permissao("CARGO_ATRIBUIR", "Atribuir cargo a usuários", "CARGOS"),
                    new Permissao("PERMISSAO_LISTAR", "Listar permissões", "PERMISSOES"),
                    new Permissao("EMPRESTIMO_SOLICITAR", "Solicitar empréstimo", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EDITAR", "Editar empréstimo", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EXCLUIR", "Excluir empréstimo", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_LISTAR", "Listar empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_LIBERAR", "Liberar empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_LISTAR_TODOS", "Listar todos os empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EDITAR_TODOS", "Editar todos os empréstimos", "EMPRESTIMOS"),
                    new Permissao("EMPRESTIMO_EXCLUIR_TODOS", "Excluir todos os empréstimos", "EMPRESTIMOS"),
                    new Permissao("ITEM_PATRIMONIO_CADASTRAR", "Cadastrar item de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("ITEM_PATRIMONIO_EDITAR", "Editar item de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("ITEM_PATRIMONIO_EXCLUIR", "Excluir item de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("ITEM_PATRIMONIO_LISTAR", "Listar itens de patrimônio", "ITENS_PATRIMONIO"),
                    new Permissao("PATRIMONIO_CADASTRAR", "Cadastrar patrimônio", "PATRIMONIO"),
                    new Permissao("PATRIMONIO_EDITAR", "Editar patrimônio", "PATRIMONIO"),
                    new Permissao("PATRIMONIO_EXCLUIR", "Excluir patrimônio", "PATRIMONIO"),
                    new Permissao("PATRIMONIO_LISTAR", "Listar patrimônios", "PATRIMONIO"),
                    new Permissao("CATEGORIA_CADASTRAR", "Cadastrar categoria", "CATEGORIAS"),
                    new Permissao("CATEGORIA_EDITAR", "Editar categoria", "CATEGORIAS"),
                    new Permissao("CATEGORIA_EXCLUIR", "Excluir categoria", "CATEGORIAS"),
                    new Permissao("CATEGORIA_LISTAR", "Listar categorias", "CATEGORIAS")
            );
            permissaoRepository.saveAll(lista);
            log.info("Permissões padrão inicializadas com sucesso.");
        }
    }

    private void cargoAdmin() {
        if (cargoRepository.findByNome("Administrador").isEmpty()) {
            List<Permissao> todasPermissoes = permissaoRepository.findAll();
            Cargo cargo = new Cargo("Administrador", new HashSet<>(todasPermissoes));
            cargoRepository.save(cargo);
            log.info("Cargo Administrador criado com sucesso com todas as permissões.");
        } else {
            log.info("Cargo admin já existe.");
        }
    }

    private void cargoUsuario() {
        if (cargoRepository.findByNome("Usuário").isEmpty()) {
            Set<String> nomesPermissoes = Set.of(
                    "USUARIO_EDITAR", "EMPRESTIMO_EDITAR", "EMPRESTIMO_EXCLUIR",
                    "EMPRESTIMO_LISTAR", "EMPRESTIMO_SOLICITAR", "PATRIMONIO_LISTAR",
                    "ITEM_PATRIMONIO_LISTAR", "CATEGORIA_LISTAR", "USUARIO_LISTAR"
            );
            List<Permissao> todas = permissaoRepository.findAll();
            Set<Permissao> permissoesUsuario = todas.stream()
                    .filter(p -> nomesPermissoes.contains(p.getNome()))
                    .collect(Collectors.toSet());

            Cargo cargo = new Cargo("Usuário", permissoesUsuario);
            cargoRepository.save(cargo);
            log.info("Cargo Usuário criado com sucesso.");
        } else {
            log.info("Cargo usuário já existe.");
        }
    }
}
