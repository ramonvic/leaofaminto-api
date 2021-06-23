package com.github.jeancsanchez.leaofaminto.data

import com.github.jeancsanchez.leaofaminto.domain.model.Imposto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

@Repository
interface ImpostoRepository : JpaRepository<Imposto, Long> {

    fun findTop1ByDataReferenciaAndValor(
        dataReferencia: LocalDate,
        valor: Double
    ): Imposto?

    fun findAllByEstaPago(estaPago: Boolean): List<Imposto>
}