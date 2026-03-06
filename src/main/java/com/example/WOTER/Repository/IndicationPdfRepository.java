package com.example.WOTER.Repository;

import com.example.WOTER.SaldoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IndicationPdfRepository extends JpaRepository<SaldoEntity, Long> {
    @Query(value = """          
              select ws.client_id id,
                     flat,
                     wc.pers_account,
                     wc.client_name,
                     wc.client_type_id,
                     CASE
                        WHEN wa.street_id IS NULL THEN
                                s2.street_name || ' ' || h.house || ' кв. ' || wa.flat
                        ELSE
                            s.street_name || ' д. ' || wa.flat
                        END AS address,
                     ws.debet_in,
                     ws.credet_in,
                     ws.charged_money,
                     ws.tax_in,
                     ws.payd_in,
                     ws.tax_out,
                     ws.debet_out,
                     ws.credet_out,
                     ws.subsidy,
                     ws.date_calc,
                     ws.month_id,
                     ws.year_id,
                     ws.removal_in,
                     ws.rem_tax_in,
                     (ws.debet_out + round((ws.debet_out  * (select tax_rate from wot_taxes where id = 1)), 2)) as summa,
                     ws.PERSONS_OUT,
                     ws.PERS_FACT_OUT,
                     ws.PERS_RESULT_OUT,
                     round(debet_out * (select tax_rate from wot_taxes wt where wt.id = 1),2) as summa_tax,
                     (select tax_rate from wot_taxes wt where wt.id = 1) as tax,
                     (select tax from wot_taxes wt where wt.id = 1) as tax_prcent,
                     row_number() OVER (ORDER BY wc.id) rownum,
                     (select wt.tarif
                            from wot_tariffs wt
                            where wt.tarif_status_id = 1
                            and wt.tarif_date >= to_date('2025-09-15','yyyy-mm-dd')
                            and wt.system_id = 1
                            and wt.tarif_type_id = 3)as tarif,
                     (
                         select to_char(max(case when rn = 2 then data_calc end), 'DD.MM.YYYY')
                         from (
                             select date_calc,
                                    to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') as data_calc,
                                    row_number() over (order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc) rn
                             from wot_clients_counters_ind wi2
                             where wi2.client_id = wc.id
                             order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc
                             limit 2
                         ) t
                       ) as date_prev,
                       (
                         select to_char(max(case when rn = 1 then data_calc end), 'DD.MM.YYYY')
                         from (
                             select date_calc,
                                    to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') as data_calc,
                                    row_number() over (order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc) rn
                             from wot_clients_counters_ind wi3
                             where wi3.client_id = wc.id
                             order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc
                             limit 2
                         ) t
                       ) as date_curr,
                       (
                         select max(case when rn = 2 then indication end)
                         from (
                             select indication,
                                    to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') as data_calc,
                                    row_number() over (order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc) rn
                             from wot_clients_counters_ind wi4
                             where wi4.client_id = wc.id
                             order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc
                             limit 2
                         ) t
                       ) as indication_prev,
                       (
                         select max(case when rn = 1 then indication end)
                         from (
                             select indication,
                                    to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') as data_calc,
                                    row_number() over (order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc) rn
                             from wot_clients_counters_ind wi5
                             where wi5.client_id = wc.id
                             order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc
                             limit 2
                         ) t
                       ) as indication_curr,
                       (
                         select max(case when rn = 1 then m3 end)
                         from (
                             select m3,
                                    to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') as data_calc,
                                    row_number() over (order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc) rn
                             from wot_clients_counters_ind wi6
                             where wi6.client_id = wc.id
                             order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc
                             limit 2
                         ) t
                       ) as m3_curr,
                       (
                         select max(case when rn = 1 then summa end)
                         from (
                             select summa,
                                    to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') as data_calc,
                                    row_number() over (order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc) rn
                             from wot_clients_counters_ind wi7
                             where wi7.client_id = wc.id
                             order by to_date('01.'||month_id||'.'||year_id,'dd.mm.yyyy') desc
                             limit 2
                         ) t
                       ) as summa_curr
              from wot_clients wc
              left join wot_saldo ws on ws.client_id = wc.id
              left join wot_address wa on wa.client_id = wc.id
              left join wot_streets s on s.id = wa.street_id
              left join wot_houses h on h.id = wa.house_id
              left join wot_streets s2 on s2.id = h.street_id
              left join wot_clients_counters_ind wi on wi.client_id = wc.id
              where wc.system_id = 1
                and ws.month_id = ?
                and ws.year_id  = ?
                and wi.month_id = ws.month_id
                and wi.year_id = ws.year_id
                AND WC.COUNTER_IN_ID = 1
                and wc.status_id = 1
              order by wc.id
            """, nativeQuery = true)
    List<Object[]> findInd(@Param("month") Integer month, @Param("year") Integer year);

}
