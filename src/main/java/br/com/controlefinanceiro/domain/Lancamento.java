
package br.com.controlefinanceiro.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamentos")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoLancamento tipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaFinanceira conta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Construtor exigido pelo JPA
    protected Lancamento() {
    }

    // Construtor para criar um lançamento
    public Lancamento(
            String descricao,
            BigDecimal valor,
            LocalDate dataLancamento,
            TipoLancamento tipo,
            ContaFinanceira conta,
            Categoria categoria) {

        this.descricao = descricao;
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.tipo = tipo;
        this.conta = conta;
        this.categoria = categoria;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public ContaFinanceira getConta() {
        return conta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    // Atualização do lançamento
    public void atualizarDados(
            String descricao,
            BigDecimal valor,
            LocalDate dataLancamento,
            TipoLancamento tipo,
            Categoria categoria) {

        this.descricao = descricao;
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.tipo = tipo;
        this.categoria = categoria;
    }
}
