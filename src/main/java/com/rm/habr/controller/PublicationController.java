package com.rm.habr.controller;

import com.rm.habr.dto.CreatePublicationDto;
import com.rm.habr.dto.UpdatePublicationDto;
import com.rm.habr.model.Comment;
import com.rm.habr.model.MiniPublication;
import com.rm.habr.model.Publication;
import com.rm.habr.model.PublicationsPage;
import com.rm.habr.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/publications")
public class PublicationController {
    private final PublicationService publicationService;
    private final CommentService commentService;
    private final GenreService genreService;
    private final TagService tagService;
    private final MarkdownService markdownService;


    @Autowired
    public PublicationController(PublicationService publicationService, CommentService commentService,
                                 GenreService genreService, TagService tagService, MarkdownService markdownService) {
        this.publicationService = publicationService;
        this.commentService = commentService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.markdownService = markdownService;

    }

    @GetMapping
    public String getAllPublications(Model model,
                                     @RequestParam(value = "genre", required = false, defaultValue = "Все") String genreName,
                                     @RequestParam(defaultValue = "1") Integer page) {
        publicationService.findByGenreName(genreName, page, model);
        return "publications";
    }

    @GetMapping("/byUser")
    public String getAllPublicationsByUser(Model model,
                                           @RequestParam Long userId,
                                           @RequestParam(defaultValue = "1") Integer page) {
        publicationService.findByUserId(userId, page, model);
        return "publications";
    }

    @GetMapping("/{id}")//fixme убрать сеттание в модель
    public String getPublication(@PathVariable long id, Model model, HttpSession session) {
        Publication publication = publicationService.findById(id);
        commentService.findCommentsByPublicationId(id, model);
        publicationService.incrementViewsCount(id);

        //todo если пользователь не авторизовался, то вылетит ошибка throw not allowed instead 500
        if (session.getAttribute("userId") != null) {
            boolean isLiked = publicationService.checkUserLikedPublication(id, (Long) session.getAttribute("userId"));
            model.addAttribute("isLike", isLiked);
        }

        model.addAttribute("publication", publication);
        markdownService.getHtmlContent(publication, model);
        //fixme не сетить в модель
        model.addAttribute("newComment", new Comment());

        boolean isCanModify = (publication.getAuthor().getId().equals(session.getAttribute("userId")))
                || (session.getAttribute("isAdmin") != null);
        model.addAttribute("isCanModify", isCanModify);
        publicationService.getBestMiniPublications(model);
        return "publication-details";
    }

    @GetMapping("/add")//fixme
    public String showPublicationForm(Model model, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("forbiddenMessage", "Вы не зарегистрированы. Пожалуйста, зарегистрируйтесь и повторите попытку.");
            return "forbidden";
        }
        // todo подумать, как тут избавиться от пустого конструктора
        model.addAttribute("publication", new CreatePublicationDto());
        genreService.findAll(model);
        tagService.findAll(model);
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
        model.addAttribute("updatedPublication", UpdatePublicationDto.convert(publicationService.findById(id)));
        // fixme подумать что лучше сеттание внутри метода поиска или лучше сделать метод чистым
        genreService.findAll(model);
        tagService.findAll(model);
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
        long userId = (long) session.getAttribute("userId");
        boolean isLikedByUser = publicationService.checkUserLikedPublication(publicationId, userId);

        if (isLikedByUser) {
            publicationService.deleteLike(publicationId, userId);
        } else {
            publicationService.addLike(publicationId, userId);
        }

        return "redirect:/publications/" + publicationId;
    }

}
