package com.rm.habr.service;

import ch.qos.logback.core.util.FileUtil;
import com.rm.habr.model.BestUser;
import com.rm.habr.model.Publication;
import com.rm.habr.model.User;
import com.rm.habr.repository.PublicationRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportService {
    private final Path root = Paths.get("src/main/resources/reports/");

    private final PublicationRepository publicationRepository;

    public ReportService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public byte[] exportGetBestPublicationsReport(String reportFormat)
            throws IOException, JRException {
        List<Publication> publications = publicationRepository.findBestPublications();
        File file = ResourceUtils.getFile("classpath:reports/best_publications.jrxml");
        JasperReport report = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(publications);
        Map<String, Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
        String fileName = "C\\reports\\best_publications" + LocalDateTime.now();

        if (reportFormat.equalsIgnoreCase("html")) {
            fileName += ".html";
            JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
        } else if (reportFormat.equalsIgnoreCase("pdf")) {
            fileName += ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
        } else if (reportFormat.equalsIgnoreCase("csv")) {
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            fileName += ".csv";
            exporter.setExporterOutput(new SimpleWriterExporterOutput(new File(fileName)));
            SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
            configuration.setWriteBOM(Boolean.TRUE);
            configuration.setRecordDelimiter("\r\n");
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } else {
            throw new RuntimeException("Не верный формат отчета");
        }
        BufferedInputStream bs = new BufferedInputStream(new FileInputStream(fileName));
        byte[] bytes = bs.readAllBytes();
        bs.close();
        return bytes;
    }


    public byte[] exportGetBestUsersReport(String reportFormat)
            throws IOException, JRException {
        List<BestUser> users = publicationRepository.findBestUsers();
        File file = ResourceUtils.getFile("classpath:reports/best_users.jrxml");
        JasperReport report = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
        Map<String, Object> params = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
        String fileName = "/home/ruslooob/Documents/reports/best_users" + LocalDateTime.now();
        if (reportFormat.equalsIgnoreCase("html")) {
            fileName += ".html";
            JasperExportManager.exportReportToHtmlFile(jasperPrint, fileName);
        } else if (reportFormat.equalsIgnoreCase("pdf")) {
            fileName += ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
        } else if (reportFormat.equalsIgnoreCase("csv")) {
            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            fileName += ".csv";
            exporter.setExporterOutput(new SimpleWriterExporterOutput(new File(fileName)));
            SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
            configuration.setWriteBOM(Boolean.TRUE);
            configuration.setRecordDelimiter("\r\n");
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        } else {
            throw new RuntimeException("Не верный формат отчета");
        }
        BufferedInputStream bs = new BufferedInputStream(new FileInputStream(fileName));
        byte[] bytes = bs.readAllBytes();
        bs.close();
        return bytes;
    }
}
