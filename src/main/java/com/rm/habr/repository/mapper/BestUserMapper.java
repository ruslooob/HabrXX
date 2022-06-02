package com.rm.habr.repository.mapper;

import com.rm.habr.model.BestUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BestUserMapper implements RowMapper<BestUser> {
    @Override
    public BestUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BestUser(rs.getString("user_login"),
                rs.getInt("count"),
                rs.getInt("user_karma"));
    }
}
