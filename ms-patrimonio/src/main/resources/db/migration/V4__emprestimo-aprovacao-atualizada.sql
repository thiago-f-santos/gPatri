ALTER TABLE emprestimo
ADD COLUMN situacao VARCHAR(20) DEFAULT 'EM_ESPERA';

UPDATE emprestimo
SET situacao = CASE
    WHEN aprovado = TRUE THEN 'APROVADO'
    ELSE 'EM_ESPERA'
END;

ALTER TABLE emprestimo
DROP COLUMN aprovado;