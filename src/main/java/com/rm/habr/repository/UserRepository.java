package com.rm.habr.repository;

import com.rm.habr.dto.RegisterUserDto;
import com.rm.habr.model.User;
import com.rm.habr.repository.mapper.UserMapper;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    public static final int PAGE_SIZE = 10;

    private static final RowMapper<User> USER_ROW_MAPPER = JdbcTemplateMapperFactory.newInstance()
            .ignorePropertyNotFound()
            .newRowMapper(User.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insert(RegisterUserDto user) {
        String sql = """
                insert into _user (user_email, user_login, user_password)
                values  (:email, :login, :password)
                """;
        var params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("password", user.getPassword());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);
        return (long) keyHolder.getKeys().get("user_id");

    }

    public Optional<User> findByLogin(String login) {
        final String sql = """
                SELECT _user.user_id        AS id,
                       _user.user_email     AS email,
                       _user.user_login     AS "login",
                       _user.user_password  AS "password",
                       _user.user_karma     AS karma
                FROM _user
                WHERE _user.user_login = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, USER_ROW_MAPPER, login)
                .stream().findAny();
    }

    @Deprecated(forRemoval = true)
    public Optional<User> findByLoginAndEmail(String login, String email) {
        final String sql = """
                SELECT _user.user_id        AS id,
                       _user.user_email     AS email,
                       _user.user_login     AS "login",
                       _user.user_karma     AS karma
                FROM _user
                WHERE _user.user_login = ? AND _user.user_email = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, USER_ROW_MAPPER, login, email)
                .stream().findAny();
    }

    public Optional<User> findById(long id) {
        final String sql = """
                SELECT _user.user_id        AS id,
                       _user.user_email     AS email,
                       _user.user_login     AS "login",
                       _user.user_karma     AS karma
                FROM _user
                WHERE _user.user_id = ?
                """;
        /*избавиться от маппера*/
        return jdbcTemplate.getJdbcTemplate().query(sql, USER_ROW_MAPPER, id)
                .stream().findAny();
    }

    public boolean isUserAdmin(Long id) {
        final String sql = """
                select user_id
                from "admin"
                where user_id = ?;
                """;
        Optional<Long> userId = jdbcTemplate.getJdbcTemplate().query(sql,
                        (rs, rowNum) -> rs.getLong("user_id"),
                        id)
                .stream().findAny();
        return userId.isPresent();
    }

    public List<User> findPage(Integer page) {
        final String sql = """
                select user_id, user_email, user_login, user_karma
                from _user
                limit %d
                offset %d * (?-1)
                """.formatted(PAGE_SIZE, PAGE_SIZE);
        return jdbcTemplate.getJdbcTemplate().query(sql, new UserMapper(), page);
    }

    public Integer getUsersCount() {
        final String sql = """
                select count(*) from "_user";
                """;
        return jdbcTemplate.getJdbcTemplate().queryForObject(sql, (rs, rowNum) -> rs.getInt("count"));
    }

    public void saveAdmin(Long userId) {
        final String sql = """
                insert into "admin" (user_Id) values (?)
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, userId);
    }

    public void delete(Long userId) {
        final String sql = """
                delete from _user where user_id = ?
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, userId);
    }

}
