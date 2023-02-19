package com.rm.habr.repository;

import com.rm.habr.model.*;
import com.rm.habr.repository.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class PublicationRepository {
    public static final int PAGE_SIZE = 10;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final CommentRepository commentRepository;

    @Autowired
    public PublicationRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                 CommentRepository commentRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public long insert(Publication publication) {
        final String sql = """
                insert into "publication" (user_id, publication_header,
                publication_preview_image_path, publication_content)
                values  (:userId, :header, :previewPath, :content)
                """;

        var params = new MapSqlParameterSource()
                .addValue("userId", publication.getAuthor().getId())
                .addValue("header", publication.getHeader())
                .addValue("previewPath", publication.getPreviewImagePath())
                .addValue("content", publication.getContent());
        var keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder);
        long publicationId = (long) keyHolder.getKeys().get("publication_id");

        for (Genre genre : publication.getGenres())
            addGenre(publicationId, genre.getId());

        for (Tag tag : publication.getTags())
            addTag(publicationId, tag.getId());

        return publicationId;
    }

    private void addGenre(long publicationId, long genreId) {
        final String sql = """
                insert into relates_to (genre_id, publication_id)
                values  (:genreId, :publicationId)
                """;
        var params = new MapSqlParameterSource()
                .addValue("publicationId", publicationId)
                .addValue("genreId", genreId);

        jdbcTemplate.update(sql, params);
    }

    private void addTag(long publicationId, long tagId) {
        final String sql = """
                insert into marked (publication_id, tag_id)
                values  (:publicationId, :tagId)
                """;
        var params = new MapSqlParameterSource()
                .addValue("publicationId", publicationId)
                .addValue("tagId", tagId);

        jdbcTemplate.update(sql, params);
    }

    public List<Publication> findAll() {
        final String sql = """
                SELECT "publication".publication_id,
                       publication_views_count,
                       publication_header,
                       publication_preview_image_path,
                       publication_content,
                       publication_datetime,
                       (select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id) as publication_karma,
                       "_user".user_id,
                       user_login,
                       user_email,
                       user_karma
                FROM "publication"
                         LEFT JOIN "_user" on "_user".user_id = "publication".user_id
                ORDER BY publication_datetime DESC
                """;

        var publications = jdbcTemplate.getJdbcTemplate().query(sql, new PublicationMapper());
        publications.forEach(p -> p.setComments(commentRepository.findCommentsByPublicationId(p.getId())));
        publications.forEach(p -> p.setGenres(findGenresByPublicationId(p.getId())));
        publications.forEach(p -> p.setTags(findTagsByPublicationId(p.getId())));

        return publications;
    }

    public Publications findPage(Integer page) {
        //language=sql
        final String sql = """
                SELECT "publication".publication_id,
                       publication_views_count,
                       publication_header,
                       publication_preview_image_path,
                       publication_content,
                       publication_datetime,
                       (select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id) as publication_karma,
                       "_user".user_id,
                       user_login,
                       user_email,
                       user_karma
                FROM "publication"
                         LEFT JOIN "_user" on "_user".user_id = "publication".user_id
                ORDER BY publication_datetime DESC
                limit %d
                offset %d * (?-1)
                """.formatted(PAGE_SIZE, PAGE_SIZE);

        var publications = jdbcTemplate.getJdbcTemplate().query(sql, new PublicationMapper(), page);
        publications.forEach(p -> p.setComments(commentRepository.findCommentsByPublicationId(p.getId())));
        publications.forEach(p -> p.setGenres(findGenresByPublicationId(p.getId())));
        publications.forEach(p -> p.setTags(findTagsByPublicationId(p.getId())));

        return new Publications(publications, getPublicationsCount());
    }

    public Publications findPageByGenreName(String genreName, Integer page) {
        //language=sql
        final String sql = """
                SELECT "publication".publication_id,
                       publication_views_count,
                       publication_header,
                       publication_preview_image_path,
                       publication_content,
                       publication_datetime,
                       (select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id) as publication_karma,
                       "_user".user_id,
                       user_login,
                       user_email,
                       user_karma
                FROM "publication"
                         LEFT JOIN "_user" on "_user".user_id = "publication".user_id
                         INNER JOIN relates_to genres on "publication".publication_id = genres.publication_id
                         INNER JOIN genre on genres.genre_id = genre.genre_id
                WHERE genre.genre_name ilike ?
                ORDER BY publication_datetime DESC
                limit %d
                offset %d * (? - 1)
                """
                .formatted(PAGE_SIZE, PAGE_SIZE);

        var publications = jdbcTemplate.getJdbcTemplate()
                .query(sql, new PublicationMapper(), genreName.equalsIgnoreCase("Все") ? true : genreName, page);

        publications.forEach(p -> p.setComments(commentRepository.findCommentsByPublicationId(p.getId())));
        publications.forEach(p -> p.setGenres(findGenresByPublicationId(p.getId())));
        publications.forEach(p -> p.setTags(findTagsByPublicationId(p.getId())));

        return new Publications(publications, getPublicationsCountByGenre(genreName));
    }

    public Optional<Publication> findById(long id) {
        final String sql = """
                SELECT publication_id,
                       publication_views_count,
                       publication_header,
                       publication_content,
                       publication_datetime,
                       (select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id) as publication_karma,
                       publication_preview_image_path,
                       "_user".user_id,
                       user_login,
                       user_email,
                       user_karma
                FROM "publication"
                         LEFT JOIN "_user"  on "_user".user_id = "publication".user_id
                WHERE publication_id = ?
                """;
        var publication = jdbcTemplate.getJdbcTemplate().query(sql, new PublicationMapper(), id)
                .stream().findAny();

        publication.ifPresent(p -> p.setGenres(findGenresByPublicationId(p.getId())));
        publication.ifPresent(p -> p.setTags(findTagsByPublicationId(p.getId())));

        return publication;
    }

    public void updateHeaderAndContentById(long id, String header, String content) {
        final String sql = """
                UPDATE "publication" SET publication_header = ?, publication_content = ?
                WHERE publication_id = ?
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, header, content, id);
    }

    public void updateViewsCount(long id) {
        final String sql = """
                UPDATE "publication" SET publication_views_count = publication_views_count + 1
                WHERE publication_id = ?
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, id);
    }

    public void addLike(long publicationId, long userId) {
        final String sql = """
                INSERT INTO upwoted_p (publication_id, user_id) VALUES (?, ?)
                """;

        jdbcTemplate.getJdbcTemplate().update(sql, publicationId, userId);
    }

    public void deleteLike(long publicationId, long userId) {
        final String sql = """
                DELETE FROM upwoted_p WHERE publication_id = ? AND user_id = ?
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, publicationId, userId);
    }

    public boolean checkUpVoted(long publicationId, long userId) {
        final String sql = """
                SELECT user_id
                FROM upwoted_p
                WHERE publication_id = ? AND user_id = ?
                """;

        var userIds = jdbcTemplate.getJdbcTemplate()
                .query(sql, (rs, rowNum) -> rs.getLong("user_id"), publicationId, userId);

        return !userIds.isEmpty();
    }

    private List<Genre> findGenresByPublicationId(long publicationId) {
        final String sql = """
                SELECT genre.genre_id, genre_name
                FROM genre
                    INNER JOIN relates_to rt on genre.genre_id = rt.genre_id
                WHERE rt.publication_id = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, new GenreMapper(), publicationId);
    }

    private List<Tag> findTagsByPublicationId(long publicationId) {
        final String sql = """
                SELECT "tag".tag_id, "tag".tag_name
                FROM "tag"
                    INNER JOIN marked on "tag".tag_id = marked.tag_id
                WHERE marked.publication_id = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, new TagMapper(), publicationId);
    }

    public Integer getPublicationsCount() {
        final String sql = """
                select count(*) from "publication";
                """;
        return jdbcTemplate.getJdbcTemplate().queryForObject(sql, (rs, rowNum) -> rs.getInt("count"));
    }

    public Integer getPublicationsCountByGenre(String genreName) {
        final String sql = """
                select count(*) from "publication"
                 inner join relates_to on relates_to.publication_id = "publication".publication_id
                 inner join genre on genre.genre_id = relates_to.genre_id
                 where genre.genre_name = ?;
                """;
        return jdbcTemplate.getJdbcTemplate().queryForObject(sql, (rs, rowNum) -> rs.getInt("count"), genreName);
    }

    public Integer getPublicationsCountByUser(Long userId) {
        final var sql = """
                select count(*)from "publication" where "publication".user_id = ?;
                """;
        return jdbcTemplate.getJdbcTemplate().queryForObject(sql, (rs, rowNum) -> rs.getInt("count"), userId);
    }


    public Publications findByUserId(Long userId, Integer page) {

        final String sql = """
                SELECT "publication".publication_id,
                       publication_views_count,
                       publication_header,
                       publication_preview_image_path,
                       publication_content,
                       publication_datetime,
                       (select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id) as publication_karma,
                       "_user".user_id,
                       user_login,
                       user_email,
                       user_karma
                FROM "publication"
                         LEFT JOIN "_user" on "_user".user_id = "publication".user_id
                         INNER JOIN relates_to genres on "publication".publication_id = genres.publication_id
                         INNER JOIN genre on genres.genre_id = genre.genre_id
                WHERE "publication".user_id = ?
                ORDER BY publication_datetime DESC
                limit %d
                offset %d * (? - 1)
                """.formatted(PAGE_SIZE, PAGE_SIZE);

        var publications = jdbcTemplate.getJdbcTemplate().query(sql, new PublicationMapper(), userId, page);

        publications.forEach(p -> p.setGenres(findGenresByPublicationId(p.getId())));
        publications.forEach(p -> p.setTags(findTagsByPublicationId(p.getId())));

        return new Publications(publications, getPublicationsCountByUser(userId));
    }

    public void delete(long id) {
        final String sql = """
                call delete_publication(?);
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, id);
    }


    public List<BestUser> findBestUsers() {
        final String sql = """
                    select user_login,
                           count("publication".user_id),
                           "_user".user_karma
                    from "publication"
                             inner join "_user" on "_user".user_id = "publication".user_id
                    group by user_login, "_user".user_karma
                    order by count("publication".user_id) desc
                    limit %d;
                """.formatted(PAGE_SIZE);

        return jdbcTemplate.getJdbcTemplate().query(sql, new BestUserMapper());

    }

    public List<Publication> findBestPublications() {
        final String sql = """
                SELECT publication_id,
                       publication_views_count,
                       publication_header,
                       publication_content,
                       publication_datetime,
                       (select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id) as publication_karma,
                       publication_preview_image_path,
                       "_user".user_id,
                       user_login,
                       user_email,
                       user_karma
                FROM "publication"
                         LEFT JOIN "_user"  on "_user".user_id = "publication".user_id
                order by publication_karma desc
                limit %d;
                """.formatted(PAGE_SIZE);

        return jdbcTemplate.getJdbcTemplate().query(sql, new PublicationMapper());
    }

    public List<MiniPublication> getBestMiniPublications() {
        final String sql = """
                select publication_id,
                       publication_header,
                       publication_views_count
                from "publication"
                where publication_datetime between now() - interval '7 days' and now()
                order by ((select count(*) 
                       from upwoted_p 
                       where upwoted_p.publication_id = publication.publication_id), publication_views_count) desc
                limit 5;
                """;

        var miniPublications = jdbcTemplate.getJdbcTemplate().query(sql, new MiniPublicationMapper());
        miniPublications.forEach(mp -> {
            var comments = commentRepository.findCommentsByPublicationId(mp.getId());
            mp.setCommentsCount(comments.size());
        });
        return miniPublications;
    }
}
