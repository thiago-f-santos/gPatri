CREATE TABLE IF NOT EXISTS permissoes (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    categoria VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Inserir permissões existentes no catálogo
INSERT INTO permissoes (id, nome, descricao, categoria) VALUES
    (gen_random_uuid(), 'ACESSO_ADMIN', 'Acesso administrativo geral ao sistema', 'ADMIN'),
    (gen_random_uuid(), 'USUARIO_CADASTRAR', 'Cadastrar novos usuários', 'USUARIOS'),
    (gen_random_uuid(), 'USUARIO_EDITAR', 'Editar usuários existentes', 'USUARIOS'),
    (gen_random_uuid(), 'USUARIO_EXCLUIR', 'Excluir usuários', 'USUARIOS'),
    (gen_random_uuid(), 'USUARIO_LISTAR', 'Listar e visualizar usuários', 'USUARIOS'),
    (gen_random_uuid(), 'CARGO_CADASTRAR', 'Cadastrar novos cargos', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_EDITAR', 'Editar cargos e suas permissões', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_EXCLUIR', 'Excluir cargos', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_LISTAR', 'Listar e visualizar cargos', 'CARGOS'),
    (gen_random_uuid(), 'CARGO_ATRIBUIR', 'Atribuir cargo a usuários', 'CARGOS'),
    (gen_random_uuid(), 'PERMISSAO_LISTAR', 'Listar catálogo de permissões do sistema', 'PERMISSOES'),
    (gen_random_uuid(), 'EMPRESTIMO_SOLICITAR', 'Solicitar novo empréstimo', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EDITAR', 'Editar empréstimos próprios', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EXCLUIR', 'Excluir empréstimos próprios', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_LISTAR', 'Listar empréstimos próprios', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_LIBERAR', 'Aprovar e liberar empréstimos', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_LISTAR_TODOS', 'Listar todos os empréstimos do sistema', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EDITAR_TODOS', 'Editar qualquer empréstimo do sistema', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'EMPRESTIMO_EXCLUIR_TODOS', 'Excluir qualquer empréstimo do sistema', 'EMPRESTIMOS'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_CADASTRAR', 'Cadastrar novos itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_EDITAR', 'Editar itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_EXCLUIR', 'Excluir itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'ITEM_PATRIMONIO_LISTAR', 'Listar itens de patrimônio', 'ITENS_PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_CADASTRAR', 'Cadastrar novos patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_EDITAR', 'Editar patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_EXCLUIR', 'Excluir patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'PATRIMONIO_LISTAR', 'Listar patrimônios', 'PATRIMONIO'),
    (gen_random_uuid(), 'CATEGORIA_CADASTRAR', 'Cadastrar categorias de patrimônio', 'CATEGORIAS'),
    (gen_random_uuid(), 'CATEGORIA_EDITAR', 'Editar categorias de patrimônio', 'CATEGORIAS'),
    (gen_random_uuid(), 'CATEGORIA_EXCLUIR', 'Excluir categorias de patrimônio', 'CATEGORIAS'),
    (gen_random_uuid(), 'CATEGORIA_LISTAR', 'Listar categorias de patrimônio', 'CATEGORIAS')
ON CONFLICT (nome) DO NOTHING;

-- Criar tabela temporária para migrar cargo_permissoes caso exista tabela legada com coluna string
CREATE TABLE IF NOT EXISTS cargo_permissoes_new (
    cargo_id UUID NOT NULL,
    permissao_id UUID NOT NULL,
    PRIMARY KEY (cargo_id, permissao_id),
    CONSTRAINT fk_cargo_permissoes_cargo FOREIGN KEY (cargo_id) REFERENCES cargos(id) ON DELETE CASCADE,
    CONSTRAINT fk_cargo_permissoes_permissao FOREIGN KEY (permissao_id) REFERENCES permissoes(id) ON DELETE CASCADE
);

-- Se cargo_permissoes antiga existir e tiver coluna 'permissao', migrar dados
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'cargo_permissoes' AND column_name = 'permissao'
    ) THEN
        INSERT INTO cargo_permissoes_new (cargo_id, permissao_id)
        SELECT cp.cargo_id, p.id
        FROM cargo_permissoes cp
        JOIN permissoes p ON p.nome = cp.permissao
        ON CONFLICT DO NOTHING;

        DROP TABLE cargo_permissoes;
    END IF;
END $$;

ALTER TABLE IF EXISTS cargo_permissoes_new RENAME TO cargo_permissoes;
