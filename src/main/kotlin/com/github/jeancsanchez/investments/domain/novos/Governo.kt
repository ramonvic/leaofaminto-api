package com.github.jeancsanchez.investments.domain.novos

import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */

@Entity
class Governo(

    @OneToMany
    var bolsas: List<Bolsa>,

    @OneToMany
    var corretoras: List<Corretora>,

    @OneToOne
    @Enumerated(EnumType.STRING)
    var paisDeOrigem: Pais = Pais.BR
) {

    fun taxarOperacao(operacao: Operacao): Imposto? {
        return null
    }

    enum class Pais {
        BR, EUA
    }
}