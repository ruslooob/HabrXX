package com.rm.habr.repository;

import com.rm.habr.dto.CreateCommentDto;
import com.rm.habr.model.AdminComment;
import com.rm.habr.model.Comment;
import com.rm.habr.repository.mapper.AdminCommentMapper;
import com.rm.habr.repository.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository {
    private static final int PAGE_SIZE = 10;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*todo перевести в admin comment repository*/
    public List<AdminComment> findAllComments(Integer page) {
        final String sql = """
                select comment_id,
                       comment_content,
                       user_login,
                       comment_karma,
                       comment_datetime,
                       publication_header
                from "comment"
                         inner join "_user" on "comment".user_id = "_user".user_id
                         inner join "publication" on "comment".publication_id = "publication".publication_id
                limit %d
                offset %d * (?-1)
                """.formatted(PAGE_SIZE, PAGE_SIZE);

        return jdbcTemplate.getJdbcTemplate().query(sql, new AdminCommentMapper(), page);
    }

    public long insert(CreateCommentDto comment) {
        final String sql = """
                insert into "comment" (user_id, publication_id, comment_content)
                values  (:userId, :publicationId, :content)
                """;
        var params = new MapSqlParameterSource()
                .addValue("userId", comment.getAuthor().getId())
                .addValue("publicationId", comment.getPublicationId())
                .addValue("content", comment.getContent());
        var keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(sql, params, keyHolder);
        return (long) keyHolder.getKeys().get("comment_id");
    }

    public List<Comment> findCommentsByPublicationId(long publicationId) {
        final String sql = """
                SELECT comment_id       AS id,
                       publication_id,
                       comment_content  AS "content",
                       comment_datetime AS "datetime",
                       comment_karma    AS karma,
                       "_user".user_id,
                       user_email,
                       user_login,
                       user_karma
                FROM "comment"
                    LEFT JOIN "_user" on "_user".user_id = "comment".user_id
                WHERE publication_id = ?
                """;
        return jdbcTemplate.getJdbcTemplate().query(sql, new CommentMapper(), publicationId);
    }

    public void delete(long id) {
        final String sql = """
                DELETE FROM "comment" WHERE comment_id = ?
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, id);
    }

    public Integer getCommentsCount() {
        final String sql = """
                select count(*) from "comment";
                """;
        return jdbcTemplate.getJdbcTemplate().queryForObject(sql, (rs, rowNum) -> rs.getInt("count"));
    }

}
