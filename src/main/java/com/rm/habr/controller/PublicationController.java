package com.rm.habr.controller;

import com.rm.habr.dto.CreatePublicationDto;
import com.rm.habr.dto.UpdatePublicationDto;
import com.rm.habr.model.*;
import com.rm.habr.service.*;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
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
    private final UserService userService;

    @Autowired
    public PublicationController(PublicationService publicationService, CommentService commentService,
                                 GenreService genreService, TagService tagService,
                                 UserService userService) {
        this.publicationService = publicationService;
        this.commentService = commentService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public String getAllPublications(Model model,
                                     @RequestParam(value = "genre", required = false, defaultValue = "Все") String genreName,
                                     @RequestParam(defaultValue = "1") Integer page) {
        Publications publications = publicationService.findByGenreName(genreName, page);
        model.addAttribute("publications", publications.getPublications());
        model.addAttribute("pagesCount", publications.getRowsCount() / 11 + 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("chosenFilter", genreName);
        List<MiniPublication> miniPublications = publicationService.getBestMiniPublications();
        model.addAttribute("miniPublications", miniPublications);
        return "publications";
    }

    @GetMapping("/byUser")
    public String getAllPublicationsByUser(Model model,
                                           @RequestParam Long userId,
                                           @RequestParam(defaultValue = "1") Integer page) {
        Publications publications = publicationService.findByUserId(userId, page);
        User userById = userService.findUserById(userId);
        model.addAttribute("publications", publications.getPublications());
        model.addAttribute("chosenFilter", userById.getLogin());
        model.addAttribute("pagesCount", publications.getRowsCount() / 11 + 1);
        model.addAttribute("currentPage", page);
        List<MiniPublication> miniPublications = publicationService.getBestMiniPublications();
        model.addAttribute("miniPublications", miniPublications);
        return "publications";
    }

    @GetMapping("/{id}")
    public String getPublication(@PathVariable long id, Model model, HttpSession session) {
        Publication publication = publicationService.findById(id);
        List<Comment> comments = commentService.findCommentsByPublicationId(id);
        publicationService.incrementViewsCount(id);
        //todo если пользователь не авторизовался, то вылетит ошибка throw not allowed instead 500
        if (session.getAttribute("userId") != null) {
            boolean isLiked = publicationService.checkUserLikedPublication(id, (Long) session.getAttribute("userId"));
            model.addAttribute("isLike", isLiked);
        }

        /* todo вынести это в отдельный сервис MatrkDownService*/
        model.addAttribute("publication", publication);
        List<Extension> extensions = List.of(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                InsExtension.create()
        );
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(publication.getContent());
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().extensions(extensions).build();
        model.addAttribute("htmlContent", htmlRenderer.render(document));
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new Comment());
        boolean isCanModify = (publication.getAuthor().getId().equals(session.getAttribute("userId")))
                || (session.getAttribute("isAdmin") != null);
        model.addAttribute("isCanModify", isCanModify);

        List<MiniPublication> miniPublications = publicationService.getBestMiniPublications();
        model.addAttribute("miniPublications", miniPublications);
        return "publication-details";
    }

    @GetMapping("/add")
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

    @GetMapping("/update")
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
