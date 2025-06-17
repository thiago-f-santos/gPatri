package br.edu.ifg.numbers.gpatri.msusuarios.config;

import br.edu.ifg.numbers.gpatri.msusuarios.enums.PermissaoEnum;

import java.util.*;

public class CargoPermissaoProvider {

    private static final Map<String, Set<PermissaoEnum>> PERMISSOES_POR_CARGO;

    static {
        PERMISSOES_POR_CARGO = new HashMap<>();

        Set<PermissaoEnum> permissoesAdmin = new HashSet<>();
        Collections.addAll(permissoesAdmin, PermissaoEnum.values());
        PERMISSOES_POR_CARGO.put("ADMIN", Collections.unmodifiableSet(permissoesAdmin));

        Set<PermissaoEnum> permissoesCoordenador = new HashSet<>();
        Collections.addAll(permissoesCoordenador,
                PermissaoEnum.CADASTRAR_USUARIO, PermissaoEnum.EDITAR_USUARIO, PermissaoEnum.EXCLUIR_USUARIO,
                PermissaoEnum.VISUALIZAR_USUARIO, PermissaoEnum.LISTAR_USUARIOS, PermissaoEnum.LIBERAR_EMPRESTIMO,
                PermissaoEnum.ATUALIZAR_EMPRESTIMO, PermissaoEnum.VISUALIZAR_EMPRESTIMO
                );
        PERMISSOES_POR_CARGO.put("COORDENADOR_LABORATORIO", Collections.unmodifiableSet(permissoesCoordenador));

        Set<PermissaoEnum> permissoesGerente = new HashSet<>();
        Collections.addAll(permissoesGerente,
                PermissaoEnum.CADASTRAR_PATRIMONIO, PermissaoEnum.EDITAR_PATRIMONIO, PermissaoEnum.EXCLUIR_PATRIMONIO,
                PermissaoEnum.LISTAR_PATRIMONIOS, PermissaoEnum.CADASTRAR_CATEGORIA, PermissaoEnum.EDITAR_CATEGORIA,
                PermissaoEnum.EXCLUIR_CATEGORIA, PermissaoEnum.LISTAR_CATEGORIAS, PermissaoEnum.CADASTRAR_CONDICAO,
                PermissaoEnum.EDITAR_CONDICAO, PermissaoEnum.EXCLUIR_CONDICAO, PermissaoEnum.LISTAR_CONDICOES,
                PermissaoEnum.CADASTRAR_SITUACAO, PermissaoEnum.EDITAR_SITUACAO, PermissaoEnum.EXCLUIR_SITUACAO
        );
        PERMISSOES_POR_CARGO.put("GERENTE_PATRIMONIO", Collections.unmodifiableSet(permissoesGerente));

        Set<PermissaoEnum> permissoesUsuarioComum = new HashSet<>();
        Collections.addAll(permissoesUsuarioComum,
                PermissaoEnum.SOLICITAR_EMPRESTIMO, PermissaoEnum.VISUALIZAR_EMPRESTIMO
        );
        PERMISSOES_POR_CARGO.put("USUARIO_COMUM", Collections.unmodifiableSet(permissoesUsuarioComum));
    }

    public static Set<PermissaoEnum> getPermissoes(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return PERMISSOES_POR_CARGO.getOrDefault(cargo.trim().toUpperCase(), Collections.emptySet());
    }
}
