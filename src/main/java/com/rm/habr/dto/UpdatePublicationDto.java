package com.rm.habr.dto;

import com.rm.habr.model.Genre;
import com.rm.habr.model.Publication;
import com.rm.habr.model.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UpdatePublicationDto {
    public Long id;
    public String header;
    public String content;
    public String previewImagePath;
    public List<Genre> genres;
    public Long[] genreIds;
    public List<Tag> tags;
    public Long[] tagIds;

    public static UpdatePublicationDto convert(Publication publication) {
        UpdatePublicationDto dto = new UpdatePublicationDto();
        dto.setId(publication.getId());
        dto.setHeader(publication.getHeader());
        dto.setContent(publication.getContent());
        dto.setPreviewImagePath(publication.getPreviewImagePath());
        dto.setGenres(publication.getGenres());
        dto.setGenreIds(publication.getGenres().stream().map(Genre::getId).toArray(Long[]::new));
        dto.setTags(publication.getTags());
        dto.setTagIds(publication.getTags().stream().map(Tag::getId).toArray(Long[]::new));
        return dto;
    }
}
