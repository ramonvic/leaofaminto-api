@file:Suppress("SpellCheckingInspection")

package com.github.jeancsanchez.leaofaminto.domain.model

import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.Corretora
import com.github.jeancsanchez.leaofaminto.view.formatToStringBR
import com.github.jeancsanchez.leaofaminto.view.round
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate
import javax.persistence.*

/**
 * @author @jeancsanchez
 * @created 10/06/2021
 * Jesus loves you.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
open class Operacao(

    @ManyToOne
    open var ativo: Ativo,

    @ManyToOne
    open var corretora: Corretora,

    open var data: LocalDate = LocalDate.now(),
    open var preco: Double,
    open var quantidade: Double
) {
    @Id
    @GeneratedValue(generator = "hashId")
    @GenericGenerator(
        name = "hashId",
        strategy = "com.github.jeancsanchez.leaofaminto.domain.model.OperacaoId"
    )
    open var id: String? = null

    var valorTotal: Double = 0.0
        protected set
        get() = quantidade * preco

    val hashId: String
        get() = data.formatToStringBR()
            .plus(ativo.codigo)
            .plus(corretora.id)
            .plus(ativo.tipoDeAtivo.name)
            .plus(preco.round())
            .plus(quantidade)

    override fun toString(): String {
        return "${data.formatToStringBR()} - ${javaClass.simpleName} - ${ativo.codigo} - $quantidade - $preco"
    }
}