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

    public ReceiptController(ReceiptPdfService pdfService, IndicationsPdfService indPdfService, ApartmentPdfService pdfServiceAppartment, ReceiptService receiptService, ApartmentService apartmentService) {
        this.pdfService = pdfService;
        this.indPdfService = indPdfService;
        this.pdfServiceAppartment = pdfServiceAppartment;
        this.receiptService = receiptService;
        this.apartmentService = apartmentService;
    }

    @GetMapping("/print/apartment")
    public void printApartment(@RequestParam("month") Integer month,
                               @RequestParam("year") Integer year,
                               HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=apartment.pdf");

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
        response.setHeader("Content-Disposition", "inline; filename=private_receipts.pdf");

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
}
