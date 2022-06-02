package com.rm.habr.repository.mapper;

import com.rm.habr.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setFullName(rs.getString("user_full_name"));
        user.setEmail(rs.getString("user_email"));
        user.setLogin(rs.getString("user_login"));
        user.setKarma(rs.getShort("user_karma"));
        return user;
    }
}
