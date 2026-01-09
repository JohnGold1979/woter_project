package com.example.WOTER.Repository;

import com.example.WOTER.SaldoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<SaldoEntity, Long> {

    @Query(value = """          
                       select ws.client_id id, flat, wc.pers_account, wc.client_name, wc.client_type_id,
                              ((select street_name from wot_streets where id = wa.STREET_ID) ||' д. '|| wa.FLAT ) as addressHo,
                              ((select street_name from wot_streets where id = wa.STREET_ID) ||' д. '|| wa.FLAT ) as addressPop,
                              ws.debet_in, ws.credet_in, ws.charged_money, ws.tax_in, ws.payd_in,
                              ws.tax_out, ws.debet_out, ws.credet_out, ws.subsidy,
                              ws.date_calc, ws.month_id, ws.year_id, ws.removal_in, ws.rem_tax_in,
                              (ws.debet_out + round((ws.debet_out  * (select tax_rate from wot_taxes where id = 1)), 2)) as summa,
                              ws.PERSONS_OUT, ws.PERS_FACT_OUT, ws.PERS_RESULT_OUT,
                              round(debet_out * (select tax_rate from wot_taxes wt where wt.id = 1),2) as summa_tax,
                             (select tax_rate from wot_taxes wt where wt.id = 1) as tax,
                             (select tax from wot_taxes wt where wt.id = 1) as tax_prcent,
                             row_number() OVER (ORDER BY wc.id) rownum,
                             (select wt.tarif
                              from wot_tariffs wt
                              where wt.tarif_status_id = 1
                                and wt.tarif_date >= to_date('2025-09-15','yyyy-mm-dd')
                                 and wt.tarif_type_id = wc.client_type_id
                                 and wt.system_id = 1)as tarif
                       from wot_clients wc
                       left join wot_saldo ws on ws.client_id = wc.id
                       left join wot_address wa on wa.client_id = wc.id
                       left join wot_streets s on s.id = wa.street_id
                       left join wot_houses h on h.id = wa.house_id
                       left join wot_streets s2 on s2.id = h.street_id
                       where wc.system_id = 1
                        and ws.month_id = :month
                        and ws.year_id  = :year
                        AND WC.COUNTER_IN_ID = 0
                        and wc.client_type_id = 2
                        and wc.status_id = 1
                        order by id
            """, nativeQuery = true)
    List<Object[]> findReceipts(@Param("month") Integer month, @Param("year") Integer year);
}
