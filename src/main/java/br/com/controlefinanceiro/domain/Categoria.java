
package br.com.controlefinanceiro.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "categorias",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_categorias_nome_tipo",
                        columnNames = {"nome", "tipo"}
                )
        }
)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoLancamento tipo;

    // Construtor exigido pelo JPA
    protected Categoria() {
    }

    // Construtor para criar uma categoria
    public Categoria(String nome, TipoLancamento tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    // Atualização da categoria
    public void atualizarDados(String nome, TipoLancamento tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
}
