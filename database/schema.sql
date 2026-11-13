-- Projeto: Controle Financeiro Pessoal
-- Banco de dados: MySQL 8.4 ou superior
-- Este arquivo cria o banco e suas quatro tabelas principais.

CREATE DATABASE IF NOT EXISTS controle_financeiro
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE controle_financeiro;

-- 1. Contas bancarias, carteira ou poupanca do usuario.
CREATE TABLE IF NOT EXISTS contas_financeiras (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    tipo_conta VARCHAR(20) NOT NULL,
    saldo_inicial DECIMAL(12, 2) NOT NULL DEFAULT 0.00,

    CONSTRAINT pk_contas_financeiras PRIMARY KEY (id),
    CONSTRAINT chk_contas_tipo CHECK (
        tipo_conta IN ('CORRENTE', 'POUPANCA', 'CARTEIRA', 'OUTRA')
    )
) ENGINE = InnoDB;

-- 2. Categorias usadas para classificar receitas e despesas.
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(80) NOT NULL,
    tipo VARCHAR(10) NOT NULL,

    CONSTRAINT pk_categorias PRIMARY KEY (id),
    CONSTRAINT uk_categorias_nome_tipo UNIQUE (nome, tipo),
    CONSTRAINT chk_categorias_tipo CHECK (tipo IN ('RECEITA', 'DESPESA'))
) ENGINE = InnoDB;

-- 3. Lancamentos financeiros. Cada lancamento pertence a uma conta e categoria.
CREATE TABLE IF NOT EXISTS lancamentos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descricao VARCHAR(150) NOT NULL,
    valor DECIMAL(12, 2) NOT NULL,
    data_lancamento DATE NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    conta_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,

    CONSTRAINT pk_lancamentos PRIMARY KEY (id),
    CONSTRAINT chk_lancamentos_valor CHECK (valor > 0),
    CONSTRAINT chk_lancamentos_tipo CHECK (tipo IN ('RECEITA', 'DESPESA')),

    CONSTRAINT fk_lancamentos_conta
        FOREIGN KEY (conta_id)
        REFERENCES contas_financeiras (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_lancamentos_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_lancamentos_data (data_lancamento),
    INDEX idx_lancamentos_conta_data (conta_id, data_lancamento),
    INDEX idx_lancamentos_categoria_data (categoria_id, data_lancamento)
) ENGINE = InnoDB;

-- 4. Limite de gastos por categoria em cada mes/ano.
CREATE TABLE IF NOT EXISTS orcamentos_mensais (
    id BIGINT NOT NULL AUTO_INCREMENT,
    mes TINYINT UNSIGNED NOT NULL,
    ano SMALLINT UNSIGNED NOT NULL,
    limite DECIMAL(12, 2) NOT NULL,
    categoria_id BIGINT NOT NULL,

    CONSTRAINT pk_orcamentos_mensais PRIMARY KEY (id),
    CONSTRAINT chk_orcamentos_mes CHECK (mes BETWEEN 1 AND 12),
    CONSTRAINT chk_orcamentos_ano CHECK (ano >= 2000),
    CONSTRAINT chk_orcamentos_limite CHECK (limite > 0),
    CONSTRAINT uk_orcamento_categoria_mes_ano UNIQUE (categoria_id, mes, ano),

    CONSTRAINT fk_orcamentos_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB;

-- Observacao: o saldo atual nao e armazenado como coluna.
-- Ele sera calculado no backend: saldo_inicial + receitas - despesas.
-- As regras que verificam se categoria e lancamento possuem o mesmo tipo,
-- e se um orcamento e exclusivo para despesas, serao implementadas no Service.
