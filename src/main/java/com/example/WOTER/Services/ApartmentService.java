package com.example.WOTER.Services;

import com.example.WOTER.DTO.ReceiptDTO;
import com.example.WOTER.Repository.ApartmentRepository;
import com.example.WOTER.Repository.IndicationPdfRepository;
import com.example.WOTER.Repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentService {
        private final IndicationPdfRepository repository;
        private final ApartmentRepository repositoryAppartment;

        public ApartmentService(IndicationPdfRepository repository, ApartmentRepository repositoryAppartment) {
            this.repository = repository;
            this.repositoryAppartment = repositoryAppartment;
        }

        public List<ReceiptDTO> getReceiptsApartment(Integer month, Integer year) {
            List<Object[]> rows = repositoryAppartment.findApartment(month, year);
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
        List<Object[]> rows = repository.findInd(month, year);
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
}
