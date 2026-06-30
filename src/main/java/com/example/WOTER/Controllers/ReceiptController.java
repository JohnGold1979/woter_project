package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.ReceiptDTO;
import com.example.WOTER.Services.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReceiptController {

    private final ReceiptPdfService pdfService;
    private final IndicationsPdfService indPdfService;
    private final ApartmentPdfService pdfServiceAppartment;
    private final ReceiptService receiptService;
    private final ApartmentService apartmentService;
    private final com.example.WOTER.Repository.ClientRepository clientRepository;

    public ReceiptController(ReceiptPdfService pdfService, IndicationsPdfService indPdfService, ApartmentPdfService pdfServiceAppartment, ReceiptService receiptService, ApartmentService apartmentService, com.example.WOTER.Repository.ClientRepository clientRepository) {
        this.pdfService = pdfService;
        this.pdfServiceAppartment = pdfServiceAppartment;
        this.indPdfService = indPdfService;
        this.receiptService = receiptService;
        this.apartmentService = apartmentService;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/print/apartment")
    public void printApartment(@RequestParam("month") Integer month,
                               @RequestParam("year") Integer year,
                               HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=apartment_" + month + "_" + year + ".pdf"
        );
         List<ReceiptDTO> receipts = apartmentService.getReceiptsApartment(month, year);

        byte[] pdfBytes = pdfServiceAppartment.generatePdf(receipts);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }


    @GetMapping("/print/private")
    public void printPrivate(@RequestParam("month") Integer month,
                             @RequestParam("year") Integer year,
                             HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=private_receipts_" + month + "_" + year + ".pdf"
        );

        List<ReceiptDTO> receipts = receiptService.getReceipts(month, year);

        byte[] pdfBytes = pdfService.generatePdf(receipts);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/print/indications")
    public void printIndication(@RequestParam("month") Integer month,
                                @RequestParam("year") Integer year,
                             HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=indications.pdf");

        List<ReceiptDTO> receipts = receiptService.getReceiptsInd(month, year);

        byte[] pdfBytes = indPdfService.indicationsPdf(receipts);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    @GetMapping("/print/single")
    public void printSingleReceipt(@RequestParam("persAcc") String persAcc,
                                   @RequestParam("month") Integer month,
                                   @RequestParam("year") Integer year,
                                   HttpServletResponse response) throws Exception {
        com.example.WOTER.DTO.ClientDTO client = clientRepository.findByPersAcc(persAcc);
        if (client == null) {
            response.sendError(404, "Клиент не найден");
            return;
        }

        response.setContentType("application/pdf");
        String filename = "receipt_" + persAcc + "_" + month + "_" + year + ".pdf";
        response.setHeader("Content-Disposition", "inline; filename=" + filename);

        byte[] pdfBytes;
        if (client.getClientType() != null && client.getClientType() == 2) {
            // Частный сектор
            java.util.List<ReceiptDTO> list = receiptService.getReceipts(month, year).stream()
                    .filter(r -> r.getPersAccount() != null && r.getPersAccount().equals(persAcc))
                    .toList();
            pdfBytes = pdfService.generatePdf(list);
        } else if (client.getCounterInId() != null && client.getCounterInId() == 1) {
            // Показания (водомер)
            java.util.List<ReceiptDTO> list = receiptService.getReceiptsInd(month, year).stream()
                    .filter(r -> r.getPersAccount() != null && r.getPersAccount().equals(persAcc))
                    .toList();
            pdfBytes = indPdfService.indicationsPdf(list);
        } else {
            // Квартирный сектор
            java.util.List<ReceiptDTO> list = apartmentService.getReceiptsApartment(month, year).stream()
                    .filter(r -> r.getPersAccount() != null && r.getPersAccount().equals(persAcc))
                    .toList();
            pdfBytes = pdfServiceAppartment.generatePdf(list);
        }

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
