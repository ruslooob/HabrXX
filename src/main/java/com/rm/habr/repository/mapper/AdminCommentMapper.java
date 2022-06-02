package com.rm.habr.repository.mapper;

import com.rm.habr.model.AdminComment;
import com.rm.habr.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//todo подумать над тем, чтобы сделать мапперы компонентами
public class AdminCommentMapper implements RowMapper<AdminComment> {
    @Override
    public AdminComment mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminComment comment = new AdminComment();
        comment.setId(rs.getLong("comment_id"));
        comment.setContent(rs.getString("comment_content"));
        comment.setUser(new User(rs.getString("user_login")));
        comment.setKarma(rs.getInt("comment_karma"));
        comment.setDateTime(rs.getTimestamp("comment_datetime").toLocalDateTime());
        comment.setPublicationHeader(rs.getString("publication_header"));
        return comment;
    }
}
