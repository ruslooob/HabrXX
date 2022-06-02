package com.rm.habr.controller.admin;

import com.rm.habr.model.Publication;
import com.rm.habr.service.PublicationService;
import com.rm.habr.service.RightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

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
    public String getAllPublications(Model model, HttpSession httpSession) {
        if (!rightService.isUserAdmin(httpSession)) {
            model.addAttribute("forbiddenMessage", "Вы не админ");
            return "forbidden";
        }

        List<Publication> publications = publicationService.findAll();
        model.addAttribute("publications", publications);
        return "admin/publications";
    }
}
