package com.rm.habr.service;

import com.rm.habr.dto.CreatePublicationDto;
import com.rm.habr.dto.UpdatePublicationDto;
import com.rm.habr.model.*;
import com.rm.habr.repository.PublicationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final MarkdownService markdownService;
    private final CommentService commentService;
    private final GenreService genreService;
    private final TagService tagService;


    public PublicationsPage findAllByPage(Integer page) {
        return publicationRepository.findPage(page);
    }

    public void fillFindAllByPageModel(Integer page, Model model) {
        PublicationsPage publicationsPage = findAllByPage(page);
        model.addAttribute("publications", publicationsPage.getPublications());
        model.addAttribute("currentPage", page);
        model.addAttribute("pagesCount", publicationsPage.getRowsCount() / 11 + 1);
    }

    public Publication findById(Long id) {
        Optional<Publication> publication = publicationRepository.findById(id);
        return publication.orElseThrow();
    }

    public void fillGetPublicationModel(long id, HttpSession session, Model model) {
        Publication publication = findById(id);
        model.addAttribute("publication", publication);
        model.addAttribute("comments", commentService.findCommentsByPublicationId(id));
        incrementViewsCount(id);

        model.addAttribute("htmlContent", markdownService.getHtmlContent(publication));
        model.addAttribute("newComment", new Comment());

        boolean isCanModify = (publication.getAuthor().getId().equals(session.getAttribute("userId")))
                || (session.getAttribute("isAdmin") != null);
        model.addAttribute("isCanModify", isCanModify);

        model.addAttribute("miniPublications", getBestMiniPublications());
    }

    public void fillShowPublicationFormModel(Model model) {
        model.addAttribute("publication", new CreatePublicationDto());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("tags", tagService.findAll());
    }

    public void fillShowUpdatePublicationFormModel(long id, Model model) {
        model.addAttribute("updatedPublication", UpdatePublicationDto.convert(findById(id)));
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("tags", tagService.findAll());
    }

    public Long save(CreatePublicationDto publicationDto, Long userId) {
        Publication publication = new Publication();
        publication.setAuthor(new User(userId));
        publication.setHeader(publicationDto.header);
        publication.setContent(publicationDto.content);
        List<Tag> tags = new ArrayList<>();
        for (Long tagId : publicationDto.tagIds) {
            tags.add(new Tag(tagId));
        }
        publication.setTags(tags);
        List<Genre> genres = new ArrayList<>();
        for (Long genreId : publicationDto.genreIds) {
            genres.add(new Genre(genreId));
        }
        publication.setGenres(genres);
        String pathToSave = fileStorageService.save(publicationDto.previewImage);
        publication.setPreviewImagePath(pathToSave);
        log.info("saved publication id={} previewImagePath={}", publication.getId(), publication.getPreviewImagePath());
        return publicationRepository.insert(publication);
    }

    public void incrementViewsCount(Long publicationId) {
        publicationRepository.updateViewsCount(publicationId);
    }

    public boolean checkUserLikedPublication(Long publicationId, Long userId) {
        return publicationRepository.checkUpVoted(publicationId, userId);
    }

    public void updateHeaderAndContentById(Long publicationId, String header, String content) {
        publicationRepository.updateHeaderAndContentById(publicationId, header, content);
    }

    public void toggleLike(Long publicationId, HttpSession session) {
        long userId = (long) session.getAttribute("userId");
        boolean isLikedByUser = checkUserLikedPublication(publicationId, userId);

        if (isLikedByUser) {
            deleteLike(publicationId, userId);
        } else {
            addLike(publicationId, userId);
        }
    }

    public void deleteLike(Long publicationId, Long userId) {
        publicationRepository.deleteLike(publicationId, userId);
    }

    public void addLike(Long publicationId, Long userId) {
        publicationRepository.addLike(publicationId, userId);
    }

    public PublicationsPage findByGenreName(String genreName, Integer page) {
        PublicationsPage publicationsPage;
        if (genreName.equalsIgnoreCase("Все")) {
            publicationsPage = publicationRepository.findPage(page);
        } else {
            publicationsPage = publicationRepository.findPageByGenreName(genreName, page);
        }
        return publicationsPage;
    }

    public void fillFindByGenreNameModel(String genreName, Integer page, Model model) {
        PublicationsPage publicationsPage = findByGenreName(genreName, page);
        model.addAttribute("publications", publicationsPage.getPublications());
        model.addAttribute("pagesCount", publicationsPage.getRowsCount() / (PublicationsPage.PAGE_SIZE + 1) + 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("chosenFilter", genreName);
        model.addAttribute("miniPublications", getBestMiniPublications());
    }

    public PublicationsPage findByUserId(Long userId, Integer page) {
        return publicationRepository.findByUserId(userId, page);
    }

    public void fillFindByUserIdModel(Long userId, Integer page, Model model) {
        PublicationsPage publicationsPage = findByUserId(userId, page);
        model.addAttribute("publications", publicationsPage.getPublications());
        model.addAttribute("pagesCount", publicationsPage.getRowsCount() / (PublicationsPage.PAGE_SIZE + 1) + 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("miniPublications", getBestMiniPublications());

        User userById = userService.findUserById(userId);
        model.addAttribute("chosenFilter", userById.getLogin());
    }


    public void delete(long id) {
        //todo подумать, удалять ли изображение или нет
        publicationRepository.delete(id);
    }

    public List<Publication> findBestPublications() {
        return publicationRepository.findBestPublications();
    }

    public List<BestUser> findBestUsers() {
        return publicationRepository.findBestUsers();
    }


    public List<MiniPublication> getBestMiniPublications() {
        return publicationRepository.getBestMiniPublications();
    }
}
