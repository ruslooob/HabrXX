package com.rm.habr.repository;

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

    private static final RowMapper<User> USER_ROW_MAPPER = JdbcTemplateMapperFactory.newInstance()
            .ignorePropertyNotFound()
            .newRowMapper(User.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long insert(User user) {
        String sql = """
                insert into "user" (user_full_name, user_email, user_login, user_password, user_karma)
                values  (:fullName, :email, :login, :password, 0)
                """;
        var params = new MapSqlParameterSource()
                .addValue("fullName", user.getFullName())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("password", user.getPassword());
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder);
        return (long) keyHolder.getKeys().get("user_id");

    }

    public Optional<User> findByLogin(String login) {
        final String sql = """
                SELECT "user".user_id        AS id,
                       "user".user_email     AS email,
                       "user".user_full_name AS full_name,
                       "user".user_login     AS "login",
                       "user".user_password  AS "password",
                       "user".user_karma     AS karma
                FROM "user"
                WHERE "user".user_login = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, USER_ROW_MAPPER, login)
                .stream().findAny();
    }

    @Deprecated(forRemoval = true)
    public Optional<User> findByLoginAndEmail(String login, String email) {
        final String sql = """
                SELECT "user".user_id        AS id,
                       "user".user_email     AS email,
                       "user".user_full_name AS full_name,
                       "user".user_login     AS login,
                       "user".user_karma     AS karma
                FROM "user"
                WHERE "user".user_login = ? AND "user".user_email = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, USER_ROW_MAPPER, login, email)
                .stream().findAny();
    }

    public Optional<User> findByLoginAndPassword(String login, String password) {
        final String sql = """
                SELECT "user".user_id        AS id,
                       "user".user_email     AS email,
                       "user".user_full_name AS full_name,
                       "user".user_login     AS "login",
                       "user".user_karma     AS karma
                FROM "user"
                WHERE "user".user_login = ? AND "user".user_password = ?
                """;

        return jdbcTemplate.getJdbcTemplate().query(sql, USER_ROW_MAPPER, login, password)
                .stream().findAny();
    }

    public Optional<User> findById(long id) {
        final String sql = """
                SELECT "user".user_id        AS id,
                       "user".user_email     AS email,
                       "user".user_full_name AS full_name,
                       "user".user_login     AS "login",
                       "user".user_karma     AS karma
                FROM "user"
                WHERE "user".user_id = ?
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

    public List<User> findAll() {
        final String sql = """
                select user_id, user_full_name, user_email, user_login, user_karma
                from "user";
                """;
        return jdbcTemplate.query(sql, new UserMapper());
    }

    public void saveAdmin(Long userId) {
        final String sql = """
                insert into "admin" (user_Id) values (?)
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, userId);
    }

    public void delete(Long userId) {
        final String sql = """
                delete from "user" where user_id = ?
                """;
        jdbcTemplate.getJdbcTemplate().update(sql, userId);
    }

}
