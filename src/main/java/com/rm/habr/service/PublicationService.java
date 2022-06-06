package com.rm.habr.service;

import com.rm.habr.dto.CreatePublicationDto;
import com.rm.habr.model.*;
import com.rm.habr.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final FileStorageService fileStorageService;

    public PublicationService(PublicationRepository publicationRepository,
                              FileStorageService fileStorageService) {
        this.publicationRepository = publicationRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Publication> findAll() {
        return publicationRepository.findAll();
    }

    public Publications findPage(Integer pageNumber) {
        return publicationRepository.findPage(pageNumber);
    }

    public Publication findById(Long id) {
        Optional<Publication> publication = publicationRepository.findById(id);
        return publication.orElseThrow();
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

    public void deleteLike(Long publicationId, Long userId) {
        publicationRepository.deleteLike(publicationId, userId);
    }

    public void addLike(Long publicationId, Long userId) {
        publicationRepository.addLike(publicationId, userId);
    }

    public Publications findPageByGenre(Long genreId, Integer page) {
        if (genreId == null) {
            return publicationRepository.findPage(page);
        }
        return publicationRepository.findPageByGenre(genreId, page);
    }

    public List<Publication> findAllByUserId(Long userId) {
        return publicationRepository.findAllByUserId(userId);
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
