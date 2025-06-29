CREATE TABLE cargos (
    id UUID PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cargo_permissoes (
    cargo_id UUID NOT NULL,
    permissao VARCHAR(50) NOT NULL,
    PRIMARY KEY (cargo_id, permissao),
    FOREIGN KEY (cargo_id) REFERENCES cargos(id)
);

CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    cargo_id UUID NOT NULL,
    nome VARCHAR(50) NOT NULL,
    sobrenome VARCHAR(100) NOT NULL,
    email VARCHAR(500) NOT NULL UNIQUE,
    senha TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cargo_id) REFERENCES cargos(id)
);