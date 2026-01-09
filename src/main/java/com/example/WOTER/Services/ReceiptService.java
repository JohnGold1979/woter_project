package com.example.WOTER.Services;

import com.example.WOTER.DTO.ReceiptDTO;
import com.example.WOTER.Repository.ApartmentRepository;
import com.example.WOTER.Repository.IndicationPdfRepository;
import com.example.WOTER.Repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiptService {
    private final IndicationPdfRepository repositoryInd;
    private final ReceiptRepository repository;

    public ReceiptService(IndicationPdfRepository repositoryInd, ReceiptRepository repository) {
        this.repositoryInd = repositoryInd;
        this.repository = repository;
    }

    public List<ReceiptDTO> getReceipts(Integer month, Integer year) {
        List<Object[]> rows = repository.findReceipts(month, year);
        return rows.stream().map(r -> {
            ReceiptDTO dto = new ReceiptDTO();
            dto.setId(r[0] != null ? ((Number) r[0]).longValue() : null);
            dto.setFlat((String) r[1]);
            dto.setPersAccount((String) r[2]);
            dto.setClientName((String) r[3]);
            dto.setClientTypeId(r[4] != null ? ((Number) r[4]).intValue() : null);
            dto.setAddressPop((String) r[5]);
            dto.setAddressHo((String) r[6]);
            dto.setDebetIn(r[7] != null ? ((Number) r[7]).doubleValue() : null);
            dto.setCreditIn(r[8] != null ? ((Number) r[8]).doubleValue() : null);
            dto.setChargedMoney(r[9] != null ? ((Number) r[9]).doubleValue() : null);
            dto.setTaxIn(r[10] != null ? ((Number) r[10]).doubleValue() : null);
            dto.setPaidIn(r[11] != null ? ((Number) r[11]).doubleValue() : null);
            dto.setTaxOut(r[12] != null ? ((Number) r[12]).doubleValue() : null);
            dto.setDebetOut(r[13] != null ? ((Number) r[13]).doubleValue() : null);
            dto.setCreditOut(r[14] != null ? ((Number) r[14]).doubleValue() : null);
            dto.setSubsidyIn(r[15] != null ? ((Number) r[15]).doubleValue() : null);
            dto.setMonthId(r[17] != null ? ((Number) r[17]).intValue() : null);
            dto.setYearId(r[18] != null ? ((Number) r[18]).intValue() : null);
            dto.setSumma(r[21] != null ? ((Number) r[21]).doubleValue() : null);
            dto.setPersonsOut(r[22] != null ? ((Number) r[22]).intValue() : null);
            dto.setSummaTax(r[25] != null ? ((Number) r[25]).doubleValue() : null);
            dto.setTax(r[26] != null ? ((Number) r[26]).doubleValue() : null);
            dto.setTaxPrcent((String) r[27]);
            dto.setRownum(r[28] != null ? ((Number) r[28]).intValue() : null);
            dto.setTarif(r[29] != null ? ((Number) r[29]).doubleValue() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ReceiptDTO> getReceiptsInd(Integer month, Integer year) {
        List<Object[]> rows = repositoryInd.findInd(month, year);
        return rows.stream().map(r -> {
            ReceiptDTO dto = new ReceiptDTO();

            dto.setId(r[0] != null ? ((Number) r[0]).longValue() : null);
            dto.setFlat((String) r[1]);
            dto.setPersAccount((String) r[2]);
            dto.setClientName((String) r[3]);
            dto.setClientTypeId(r[4] != null ? ((Number) r[4]).intValue() : null);
            dto.setAddress((String) r[5]);

            dto.setDebetIn(toDouble(r[6]));
            dto.setCreditIn(toDouble(r[7]));
            dto.setChargedMoney(toDouble(r[8]));
            dto.setTaxIn(toDouble(r[9]));
            dto.setPaidIn(toDouble(r[10]));
            dto.setTaxOut(toDouble(r[11]));
            dto.setDebetOut(toDouble(r[12]));
            dto.setCreditOut(toDouble(r[13]));
            dto.setSubsidyIn(toDouble(r[14]));

            dto.setMonthId(toInt(r[16]));
            dto.setYearId(toInt(r[17]));
            dto.setSumma(toDouble(r[20]));
            dto.setPersonsOut(toInt(r[21]));
            dto.setSummaTax(toDouble(r[24]));
            dto.setTax(toDouble(r[25]));
            dto.setTaxPrcent(r[26] != null ? r[26].toString() : null);
            dto.setRownum(toInt(r[27]));
            dto.setTarif(toDouble(r[28])); // правильный тариф

           // dto.setDatePrev(r[29] != null ? r[29].toString() : null);
           // dto.setDateCurr(r[30] != null ? r[30].toString() : null);
            dto.setIndicationPrev(r[31] != null ? r[31].toString() : null);
            dto.setIndicationCurr(r[32] != null ? r[32].toString() : null);
            dto.setM3(toInt(r[33])); // расход
            dto.setSumIndication(toDouble(r[34])); // сумма по показаниям

            return dto;
        }).collect(Collectors.toList());
    }

    private static Double toDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try {
            return Double.parseDouble(o.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(o.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }
}