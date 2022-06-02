package com.rm.habr.repository.mapper;

import com.rm.habr.model.MiniPublication;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MiniPublicationMapper implements RowMapper<MiniPublication> {
    @Override
    public MiniPublication mapRow(ResultSet rs, int rowNum) throws SQLException {
        MiniPublication miniPublication = new MiniPublication();
        miniPublication.setId(rs.getLong("publication_id"));
        miniPublication.setHeader(rs.getString("publication_header"));
        miniPublication.setViewsCount(rs.getInt("publication_views_count"));
        return miniPublication;
    }
}
