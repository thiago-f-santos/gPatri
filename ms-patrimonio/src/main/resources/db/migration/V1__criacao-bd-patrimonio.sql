CREATE TABLE condicao (
    id UUID PRIMARY KEY,
    condicao VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE categoria (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    id_categoria_mae UUID REFERENCES categoria(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE patrimonio (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco_estimado DOUBLE PRECISION NOT NULL,
    id_categoria UUID REFERENCES categoria(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE item_patrimonio (
    id UUID PRIMARY KEY,
    id_patrimonio UUID REFERENCES patrimonio(id),
    id_condicao UUID REFERENCES condicao(id),
    em_uso BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);