package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.model.TipoDeAtivo
import com.github.jeancsanchez.investments.view.round
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

/**
 * Essa classe é um service que contém todas as lógicas relacionadas a impostos.
 * Todas as informações foram retiradas do blog do Bastter
 * no seguinte link: https://bastter.com/mercado/forum/794001
 *
 * @author @jeancsanchez
 * @created 31/05/2021
 * Jesus loves you.
 */

@Service
class LeaoService(
    @Autowired private val operacaoRepository: OperacaoRepository,
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,

    ) {



    fun pegarImpostosNoMesComAcoesSwingTrade(mes: LocalDate): Double {
        val firstDay = YearMonth.from(mes).atDay(1)
        val lastDay = YearMonth.from(mes).atEndOfMonth()
        var lucros = 0.0
        var prejuizos = 0.0
        var totalDeVendas = 0.0

        vendasRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.ACAO }
            .groupBy { it.ativo.codigo }
            .flatMap { it.value }
            .map { venda ->
                comprasRepository.findAllByAtivoCodigo(venda.ativo.codigo)
                    .forEach { compra ->
                        if (compra.data.isBefore(venda.data)) {
                            if (venda.valorTotal > 0) {
                                totalDeVendas += venda.valorTotal

                                val resultado = venda.valorTotal - compra.valorTotal
                                if (resultado > 0) {
                                    lucros += resultado
                                } else if (resultado < 0) {
                                    prejuizos += resultado
                                }
                            }
                        }
                    }
            }

        if (totalDeVendas > 20000) {
            if (prejuizos > 0) {
//            Se vamos deduzir este prejuízo da base de cálculo do IR,
//            ele passa a ser OBRIGATÓRIO a informar na declaração,
//            na ficha Renda variável/Operações comuns.
                totalDeVendas -= prejuizos
            }

            // TODO: Precisa remover os custos antes
            return ((totalDeVendas * 0.15) - 0.00005).round()
        }

        return 0.0
    }
}