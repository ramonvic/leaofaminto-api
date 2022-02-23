package com.github.jeancsanchez.leaofaminto.domain.service.scenarios

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import com.github.jeancsanchez.leaofaminto.domain.model.Ativo
import com.github.jeancsanchez.leaofaminto.domain.model.Compra
import com.github.jeancsanchez.leaofaminto.domain.model.Venda
import com.github.jeancsanchez.leaofaminto.domain.model.corretoras.ClearCorretora
import com.github.jeancsanchez.leaofaminto.view.dto.ConsolidadoDTO
import com.github.jeancsanchez.leaofaminto.view.dto.OperacaoConsolidadaDTO
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 22/02/2022
 * Jesus loves you.
 */

class DomainScenarios(
    private val comprasRepository: ComprasRepository,
    private val vendasRepository: VendasRepository,
    private val gerarOperacoesConsolidadasService: GerarOperacoesConsolidadasService
) {

    fun loadGerarDeclaracaoIRPFScenario(dummyAtivo: Ativo, reportYear: LocalDate) {
        val corretora = ClearCorretora()
        val lastYear = reportYear.minusYears(1)
        val lastLastYear = lastYear.minusYears(1)
        val comprasList = listOf(
            Compra(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = corretora,
                data = lastLastYear,
            ),
            Compra(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = corretora,
                data = lastLastYear
            ),
            Compra(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = corretora,
                data = lastYear
            ),
        )

        val vendasList = listOf(
            Venda(
                ativo = dummyAtivo,
                quantidade = 10.0,
                preco = 10.0,
                corretora = corretora,
                data = lastLastYear
            )
        )

        whenever(comprasRepository.findAll()).thenAnswer { comprasList }
        whenever(vendasRepository.findAllByAtivoCodigo(any())).thenAnswer { vendasList }
        whenever(comprasRepository.findTopByAtivoCodigoOrderByDataDesc(any())).thenAnswer { comprasList.first() }
        whenever(gerarOperacoesConsolidadasService.execute(Unit)).thenAnswer {
            ConsolidadoDTO(
                items = listOf(
                    OperacaoConsolidadaDTO(
                        ativo = dummyAtivo,
                        quantidadeTotal = 200.0,
                        precoMedio = 10.0,
                        totalInvestido = 200.0
                    )
                ),
                totalInvestido = 200.0
            )
        }
    }

    fun loadPegarPosicaoAtivoAnoAnteriorEAnoCorrenteScenario(
        dummyAtivo: Ativo,
        lastYear: LocalDate,
        currentYear: LocalDate
    ) {
        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = lastYear,
                ),
                Compra(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = lastYear.plusMonths(2)
                ),
                Compra(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = currentYear
                ),
            )
        }

        whenever(vendasRepository.findAllByAtivoCodigo(any())).thenAnswer {
            listOf(
                Venda(
                    ativo = dummyAtivo,
                    quantidade = 10.0,
                    preco = 10.0,
                    corretora = ClearCorretora(),
                    data = lastYear.plusMonths(4)
                )
            )
        }
    }

    fun loadGerarDeclaracaoIRPFSomandoTodosOsAnosAnterioresScenario(ambev: Ativo) {
        val comprasList = mutableListOf<Compra>()

        whenever(comprasRepository.findAll()).thenAnswer {
            listOf(
                Compra(
                    ativo = ambev,
                    quantidade = 100.0,
                    preco = 12.54,
                    corretora = ClearCorretora(),
                    data = LocalDate.of(2019, 9, 3),
                ),
                Compra(
                    ativo = ambev,
                    quantidade = 2.0,
                    preco = 13.34,
                    corretora = ClearCorretora(),
                    data = LocalDate.of(2020, 9, 3),
                ),
                Compra(
                    ativo = ambev,
                    quantidade = 102.0,
                    preco = 15.80,
                    corretora = ClearCorretora(),
                    data = LocalDate.of(2021, 11, 10),
                ),
                Compra(
                    ativo = ambev,
                    quantidade = 102.0,
                    preco = 15.85,
                    corretora = ClearCorretora(),
                    data = LocalDate.of(2021, 4, 26),
                ),
            ).also {
                comprasList.addAll(it)
            }
        }

        whenever(vendasRepository.findAllByAtivoCodigo(any())).thenAnswer {
            listOf(
                Venda(
                    ativo = ambev,
                    quantidade = 100.0,
                    preco = 11.45,
                    corretora = ClearCorretora(),
                    data = LocalDate.of(2021, 9, 3),
                )
            )
        }

        whenever(gerarOperacoesConsolidadasService.execute(Unit)).thenAnswer {
            ConsolidadoDTO(
                items = listOf(
                    OperacaoConsolidadaDTO(
                        ativo = ambev,
                        quantidadeTotal = 206.0,
                        precoMedio = 16.33,
                        totalInvestido = 206.0
                    )
                ),
                totalInvestido = 3363.98
            )
        }

        whenever(comprasRepository.findTopByAtivoCodigoOrderByDataDesc(any())).thenAnswer {
            comprasList.first()
        }
    }
}