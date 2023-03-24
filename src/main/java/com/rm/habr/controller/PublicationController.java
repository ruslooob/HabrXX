package com.rm.habr.controller;

import com.rm.habr.dto.CreatePublicationDto;
import com.rm.habr.dto.UpdatePublicationDto;
import com.rm.habr.service.PublicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequestMapping("/publications")
public class PublicationController {
    private final PublicationService publicationService;


    @Autowired
    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;

    }

    @GetMapping
    public String getAllPublications(Model model,
                                     @RequestParam(value = "genre", required = false, defaultValue = "Все") String genreName,
                                     @RequestParam(defaultValue = "1") Integer page) {
        publicationService.fillFindByGenreNameModel(genreName, page, model);
        return "publications";
    }

    @GetMapping("/byUser")
    public String getAllPublicationsByUser(Model model,
                                           @RequestParam Long userId,
                                           @RequestParam(defaultValue = "1") Integer page) {
        publicationService.fillFindByUserIdModel(userId, page, model);
        return "publications";
    }

    @GetMapping("/{id}")//fixme убрать сеттание в модель
    public String getPublication(@PathVariable long id, Model model, HttpSession session) {
        //todo если пользователь не авторизовался, то вылетит ошибка throw not allowed instead 500
        if (session.getAttribute("userId") != null) {
            boolean isLiked = publicationService.checkUserLikedPublication(id, (Long) session.getAttribute("userId"));
            model.addAttribute("isLike", isLiked);
        }

        publicationService.fillGetPublicationModel(id, session, model);
        return "publication-details";
    }

    @GetMapping("/add")//fixme
    public String showPublicationForm(Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("forbiddenMessage", "Вы не зарегистрированы. Пожалуйста, зарегистрируйтесь и повторите попытку.");
            return "forbidden";
        }
        publicationService.fillShowPublicationFormModel(model);

        return "publication-form";
    }

    @PostMapping
    public String createPublication(Model model,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    CreatePublicationDto createPublicationDto,
                                    HttpSession session) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("forbiddenMessage", "Вы не зарегистрированы. Пожалуйста, зарегистрируйтесь и повторите попытку.");
            return "forbidden";
        }
        createPublicationDto.setPreviewImage(file);
        publicationService.save(createPublicationDto, (Long) session.getAttribute("userId"));
        return "redirect:/publications";
    }

    @GetMapping("/update")
    public String showPublicationUpdateForm(Model model, @RequestParam long id, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("forbiddenMessage", "Вы не зарегистрированы. Пожалуйста, зарегистрируйтесь и повторите попытку.");
            return "forbidden";
        }
        publicationService.fillShowUpdatePublicationFormModel(id, model);
        return "publication-update-form";
    }

    @PutMapping("/{id}")
    public String updatePublication(@PathVariable long id, UpdatePublicationDto publicationDto) {
        publicationService.updateHeaderAndContentById(id, publicationDto.header, publicationDto.content);
        return "redirect:/publications";
    }


    @DeleteMapping("/{id}")
    public String deletePublication(@PathVariable long id) {
        publicationService.delete(id);
        return "redirect:/publications";
    }

    @PostMapping("/{publicationId}/likes")
    public String addLikePublication(@PathVariable long publicationId, HttpSession session) {
        publicationService.toggleLike(publicationId, session);
        return "redirect:/publications/" + publicationId;
    }

}
