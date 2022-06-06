package com.rm.habr.controller.admin;

import com.rm.habr.service.ReportService;
import com.rm.habr.service.RightService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class ReportController {
    private final RightService rightService;
    private final ReportService reportService;

    @Autowired
    public ReportController(RightService rightService, ReportService reportService) {
        this.rightService = rightService;
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public String getReports(Model model, HttpSession session) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }

        return "admin/reports";
    }

    @GetMapping(value = "/reports/best-publications", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public Object exportBestPublicationsReport(Model model,
                                               HttpSession session,
                                               @RequestParam(required = false, defaultValue = "pdf") String reportFormat)
            throws JRException, IOException {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        return reportService.exportGetBestPublicationsReport(reportFormat);
    }

    @GetMapping(value = "/reports/best-authors", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public Object exportBestAuthorsReport(Model model,
                                          HttpSession session,
                                          @RequestParam(required = false, defaultValue = "pdf") String reportFormat)
            throws JRException, IOException {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }
        return reportService.exportGetBestUsersReport(reportFormat);
    }
}
