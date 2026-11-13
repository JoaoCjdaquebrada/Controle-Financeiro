
package br.com.controlefinanceiro.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "orcamentos_mensais",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_orcamento_categoria_mes_ano",
                        columnNames = {"categoria_id", "mes", "ano"}
                )
        }
)
public class OrcamentoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal limite;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Construtor exigido pelo JPA
    protected OrcamentoMensal() {
    }

    // Construtor para criar um orçamento
    public OrcamentoMensal(
            Integer mes,
            Integer ano,
            BigDecimal limite,
            Categoria categoria) {

        validarDados(mes, ano, limite, categoria);

        this.mes = mes;
        this.ano = ano;
        this.limite = limite;
        this.categoria = categoria;
    }

    // Validação das regras do orçamento
    private void validarDados(
            Integer mes,
            Integer ano,
            BigDecimal limite,
            Categoria categoria) {

        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException(
                    "O mês deve estar entre 1 e 12."
            );
        }

        if (ano == null || ano < 2000) {
            throw new IllegalArgumentException(
                    "O ano deve ser igual ou superior a 2000."
            );
        }

        if (limite == null || limite.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O limite deve ser maior que zero."
            );
        }

        if (categoria == null ||
                categoria.getTipo() != TipoLancamento.DESPESA) {
            throw new IllegalArgumentException(
                    "O orçamento deve utilizar uma categoria de despesa."
            );
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Integer getMes() {
        return mes;
    }

    public Integer getAno() {
        return ano;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    // Atualização do orçamento
    public void atualizarDados(
            Integer mes,
            Integer ano,
            BigDecimal limite,
            Categoria categoria) {

        validarDados(mes, ano, limite, categoria);

        this.mes = mes;
        this.ano = ano;
        this.limite = limite;
        this.categoria = categoria;
    }
}
