ALTER TABLE item_patrimonio
DROP CONSTRAINT IF EXISTS item_patrimonio_id_condicao_fkey;

ALTER TABLE item_patrimonio
DROP COLUMN IF EXISTS id_condicao;

DROP TABLE IF EXISTS condicao;

ALTER TABLE item_patrimonio
ADD COLUMN condicao_produto VARCHAR(50),
ADD COLUMN condicao_descricao TEXT,
ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 0;

ALTER TABLE item_patrimonio
DROP COLUMN IF EXISTS em_uso;

ALTER TABLE patrimonio
ADD COLUMN tipo_controle VARCHAR(50);

CREATE TABLE emprestimo (
    id UUID PRIMARY KEY,
    id_usuario UUID NOT NULL,
    aprovado BOOLEAN,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE item_emprestimo (
    id_item_patrimonio UUID NOT NULL,
    id_emprestimo UUID NOT NULL,
    quantidade INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id_item_patrimonio, id_emprestimo),
    FOREIGN KEY (id_item_patrimonio) REFERENCES item_patrimonio(id),
    FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id)
);