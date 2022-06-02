package com.rm.habr.repository.mapper;

import com.rm.habr.model.Comment;
import com.rm.habr.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CommentMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setPublicationId(rs.getLong("publication_id"));
        comment.setDateTime(rs.getTimestamp("datetime").toLocalDateTime());
        comment.setContent(rs.getString("content"));
        comment.setKarma(rs.getInt("karma"));
        comment.setUser(mapUser(rs, rowNum));
        return comment;
    }

    private User mapUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .fullName(rs.getString("user_full_name"))
                .login(rs.getString("user_login"))
                .karma(rs.getShort("user_karma"))
                .build();
    }
}
