package com.example.WOTER.Controllers;

import com.example.WOTER.DTO.CounterReportDTO;
import com.example.WOTER.DTO.DislocationDTO;
import com.example.WOTER.DTO.HouseDTO;
import com.example.WOTER.DTO.ReportRowDTO;
import com.example.WOTER.DTO.StreetDTO;
import com.example.WOTER.Services.PdfReportAccountsService;
import com.example.WOTER.Services.ReportAccountsService;
import com.lowagie.text.DocumentException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ReportAccountsController {

    private final ReportAccountsService reportService;
    private final PdfReportAccountsService pdfService;

    public ReportAccountsController(ReportAccountsService reportService, PdfReportAccountsService pdfService) {
        this.reportService = reportService;
        this.pdfService = pdfService;
    }

    @GetMapping("/report/house")
    public void reportHouse(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_house_" + month + "_" + year + ".pdf"
        );

        //Читать из дома из таблицы передать айди дома в метод getHouseReport

        try (var out = response.getOutputStream()) {
            List<HouseDTO> houses = reportService.getHouses();

            // создаём общий PDF со всеми домами
            pdfService.generateCombinedReport(houses, month, year, reportService, out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/report/private")
    public void reportPrivate(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_private_" + month + "_" + year + ".pdf"
        );

        try (var out = response.getOutputStream()) {
            List<StreetDTO> streets = reportService.getStreets();

            // создаём общий PDF со всеми домами
            pdfService.generatePrivateReport(streets, month, year, reportService, out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/report/housesall")
    public void reportHousesAll(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_all_houses_" + month + "_" + year + ".pdf"
        );

        try (var out = response.getOutputStream()) {

            // создаём общий PDF со всеми домами
            pdfService.generateAllHousesReport(month, year, reportService, out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/report/streetall")
    public void reportStreetAll(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_all_street_" + month + "_" + year + ".pdf"
        );

        try (var out = response.getOutputStream()) {

            // создаём общий PDF со всеми домами
            pdfService.generateAllStreetsReport(month, year, reportService, out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/report/dislocation")
    public void reportDislocation(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_dislocation_" + month + "_" + year + ".pdf"
        );

        try (var out = response.getOutputStream()) {
            // Get dislocation data for houses (apartment sector)
            List<DislocationDTO> housesData = reportService.getDislocationByHouses(month, year);
            // Get dislocation data for streets (private sector)
            List<DislocationDTO> streetsData = reportService.getDislocationByStreets(month, year);
            
            // Combine both lists
            List<DislocationDTO> allData = new ArrayList<>(housesData);
            allData.addAll(streetsData);
            
            pdfService.generateDislocationReport(allData, month, year, "Дома (квартирный сектор) + Улицы (частный сектор)", out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/report/counters")
    public void reportCounters(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_counters_" + month + "_" + year + ".pdf"
        );

        try (var out = response.getOutputStream()) {
            List<CounterReportDTO> counters = reportService.getCounterReport(month, year);
            pdfService.generateCountersReport(counters, month, year, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/report/payall")
    public void reportPayAll(
            @RequestParam int month,
            @RequestParam int year,
            HttpServletResponse response
    ) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "inline; filename=report_pay_at_" + month + "_" + year + ".pdf"
        );

        try (var out = response.getOutputStream()) {

            // создаём общий PDF со всеми домами
            pdfService.generateAllPaysReport(month, year, reportService, out);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
