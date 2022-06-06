package com.rm.habr.repository;

import com.rm.habr.model.Genre;
import com.rm.habr.model.Tag;
import com.rm.habr.repository.mapper.TagMapper;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public TagRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Tag> findAll() {
        final String sql = """
                SELECT "tag".tag_id, tag_name
                FROM "tag"
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, new TagMapper());
    }

}
