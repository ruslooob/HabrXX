package com.rm.habr.repository.mapper;


import com.rm.habr.model.Comment;
import com.rm.habr.model.Publication;
import com.rm.habr.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PublicationMapper implements RowMapper<Publication> {
    @Override
    public Publication mapRow(ResultSet rs, int rowNum) throws SQLException {
        Publication publication = new Publication();
        publication.setId(rs.getLong("publication_id"));
        publication.setAuthor(mapUser(rs, rowNum));
        publication.setViewsCount(rs.getInt("publication_views_count"));
        publication.setHeader(rs.getString("publication_header"));
        publication.setContent(rs.getString("publication_content"));
        publication.setPreviewImagePath(rs.getString("publication_preview_image_path"));
        publication.setPublishDateTime(rs.getTimestamp("publication_datetime").toLocalDateTime());
        publication.setKarma(rs.getInt("publication_karma"));
        return publication;
    }

    private User mapUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .fullName(rs.getString("user_full_name"))
                .login(rs.getString("user_login"))
                .email(rs.getString("user_email"))
                .karma(rs.getShort("user_karma"))
                .build();
    }


}
