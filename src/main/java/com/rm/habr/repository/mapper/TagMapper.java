package com.rm.habr.repository.mapper;

import com.rm.habr.model.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tag tag = new Tag();
        tag.setId(rs.getLong("tag_id"));
        tag.setName(rs.getString("tag_name"));
        return tag;
    }
}
