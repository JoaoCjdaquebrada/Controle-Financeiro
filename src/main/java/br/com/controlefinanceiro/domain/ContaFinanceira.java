
package br.com.controlefinanceiro.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contas_financeiras")
public class ContaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false, length = 20)
    private TipoConta tipoConta;

    @Column(name = "saldo_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoInicial;

    // Construtor exigido pelo JPA
    protected ContaFinanceira() {
    }

    // Construtor para criar uma conta
    public ContaFinanceira(String nome, TipoConta tipoConta, BigDecimal saldoInicial) {
        this.nome = nome;
        this.tipoConta = tipoConta;
        this.saldoInicial = saldoInicial;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    // Atualização dos dados da conta
    public void atualizarDados(String nome, TipoConta tipoConta) {
        this.nome = nome;
        this.tipoConta = tipoConta;
    }
}
