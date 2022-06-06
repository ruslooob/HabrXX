package com.rm.habr.repository.mapper;

import com.rm.habr.model.Genre;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getLong("genre_Id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }
}
