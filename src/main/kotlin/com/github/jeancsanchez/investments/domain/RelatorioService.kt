package com.github.jeancsanchez.investments.domain

import com.github.jeancsanchez.investments.data.ComprasRepository
import com.github.jeancsanchez.investments.data.VendasRepository
import com.github.jeancsanchez.investments.domain.model.dto.ConsolidadoDTO
import com.github.jeancsanchez.investments.domain.model.dto.OperacaoConsolidadaDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author @jeancsanchez
 * @created 21/05/2021
 * Jesus loves you.
 */

@Service
class RelatorioService(
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,
) {
    fun pegarOperacoesConsolidadas(): ConsolidadoDTO {
        val items = comprasRepository.findAll()
            .groupBy { it.ativo.codigo }
            .map { map ->
                val valorCompras = map.value
                    .sumByDouble { it.valorTotal }

                val quantidadeCompras = map.value
                    .sumBy { it.quantidade }

                val quantidadeVendas = vendasRepository.findAllByAtivoCodigo(map.key)
                    .sumBy { it.quantidade }

                val quantidade = quantidadeCompras - quantidadeVendas
                val precoMedio = if (quantidade > 0) {
                    valorCompras / quantidadeCompras
                } else {
                    0.0
                }
                val precoTotal = quantidade * precoMedio

                OperacaoConsolidadaDTO(
                    codigoAtivo = map.key,
                    quantidadeTotal = quantidade,
                    precoMedio = BigDecimal(precoMedio).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
                    totalInvestido = BigDecimal(precoTotal).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                )
            }
            .toMutableList()
            .handleJSLG()

        return ConsolidadoDTO(
            totalInvestido = items.sumByDouble { it.totalInvestido },
            items = items.sortedByDescending { it.totalInvestido }
        )
    }

    /**
     * O papel JSLG mudou o nome para SIMH. Esse método substitui as operações
     * de JSLG por SIMH. Apenas o nome.
     */
    private fun MutableList<OperacaoConsolidadaDTO>.handleJSLG(): List<OperacaoConsolidadaDTO> {
        replaceAll {
            if (it.codigoAtivo == "JSLG3") {
                it.copy(codigoAtivo = "SIMH3")
            } else {
                it
            }
        }
        return this
    }
}