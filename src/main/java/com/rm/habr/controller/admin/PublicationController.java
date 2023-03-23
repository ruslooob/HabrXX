package com.rm.habr.controller.admin;

import com.rm.habr.model.PublicationsPage;
import com.rm.habr.service.PublicationService;
import com.rm.habr.service.RightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller("AdminPublicationController")
@RequestMapping("/admin")
public class PublicationController {
    private final RightService rightService;
    private final PublicationService publicationService;

    @Autowired
    public PublicationController(RightService rightService, PublicationService publicationService) {
        this.rightService = rightService;
        this.publicationService = publicationService;
    }

    @GetMapping("/publications")
    public String getAllPublications(Model model, HttpSession session, @RequestParam(defaultValue = "1") Integer page) {
        if (!rightService.isUserAdmin(session)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }

        PublicationsPage publicationsPage = publicationService.findAllByPage(page);
        model.addAttribute("publications", publicationsPage.getPublications());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", publicationsPage.getRowsCount() / 11 + 1);
        return "admin/publications";
    }
}
