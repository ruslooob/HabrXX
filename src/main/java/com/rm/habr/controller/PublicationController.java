package com.rm.habr.controller;

import com.rm.habr.dto.CreatePublicationDto;
import com.rm.habr.dto.UpdatePublicationDto;
import com.rm.habr.model.Comment;
import com.rm.habr.model.Publication;
import com.rm.habr.service.*;
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
    private final CommentService commentService;
    private final GenreService genreService;
    private final TagService tagService;
    private final UserService userService;
    private final MarkdownService markdownService;


    @Autowired
    public PublicationController(PublicationService publicationService, CommentService commentService,
                                 GenreService genreService, TagService tagService, MarkdownService markdownService,
                                 UserService userService) {
        this.publicationService = publicationService;
        this.commentService = commentService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.userService = userService;
        this.markdownService = markdownService;

    }

    @GetMapping//fixme вынести все в publicationService
    public String getAllPublications(Model model,
                                     @RequestParam(value = "genre", required = false, defaultValue = "Все") String genreName,
                                     @RequestParam(defaultValue = "1") Integer page) {
        PublicationsPage publicationsPage = publicationService.findByGenreName(genreName, page);
        model.addAttribute("publications", publicationsPage.getPublications());
        model.addAttribute("pagesCount", publicationsPage.getRowsCount() / 10 + 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("chosenFilter", genreName);
        List<MiniPublication> miniPublications = publicationService.getBestMiniPublications();
        model.addAttribute("miniPublications", miniPublications);
        return "publications";
    }

    @GetMapping("/byUser")//fixme вынести все в publicationSErvice
    public String getAllPublicationsByUser(Model model,
                                           @RequestParam Long userId,
                                           @RequestParam(defaultValue = "1") Integer page) {
        PublicationsPage publicationsPage = publicationService.findByUserId(userId, page);
        User userById = userService.findUserById(userId);
        model.addAttribute("publications", publicationsPage.getPublications());
        model.addAttribute("chosenFilter", userById.getLogin());
        model.addAttribute("pagesCount", publicationsPage.getRowsCount() / 11 + 1);
        model.addAttribute("currentPage", page);
        List<MiniPublication> miniPublications = publicationService.getBestMiniPublications();
        model.addAttribute("miniPublications", miniPublications);
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
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("tags", tagService.findAll());
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

    @GetMapping("/update")//fixme засеттить в модель
    public String showPublicationUpdateForm(Model model, @RequestParam long id, HttpSession session) {
        if (session.getAttribute("userId") == null) {
            model.addAttribute("forbiddenMessage", "Вы не зарегистрированы. Пожалуйста, зарегистрируйтесь и повторите попытку.");
            return "forbidden";
        }
        model.addAttribute("updatedPublication", UpdatePublicationDto.convert(publicationService.findById(id)));
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("tags", tagService.findAll());
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
