create procedure delete_publication(id bigint)
    language plpgsql
as
$$
DECLARE
    cmnt_id        bigint;
    -- id комментариев, принадлежащей этой публикации
    comment_ids bigint[];
BEGIN
    comment_ids := ARRAY(SELECT comment_id FROM comment WHERE publication_id = id);
    -- удалить все лайки с комментариев
    FOREACH cmnt_id IN ARRAY comment_ids
        LOOP
            DELETE FROM upwoted_c uc WHERE uc.comment_id = cmnt_id;
        END LOOP;
    -- удалить все комментарии под публикацией
    DELETE FROM comment WHERE publication_id = id;
    -- удалить все лайки с публикации
    DELETE FROM upwoted_p WHERE publication_id = id;
    -- удалить саму публикацию
    DELETE FROM publication WHERE publication_id = id;
END;
$$;

-- не выполнять
-- CREATE FUNCTION increment_publication_karma() RETURNS TRIGGER as
-- $$
-- BEGIN
--     UPDATE publication p
--     SET publication_karma = (SELECT COUNT(publication_id)
--                              FROM publication
--                              WHERE publication_id = NEW.publication_id) + 1
--     WHERE p.publication_id = NEW.publication_id;
--     RETURN NEW;
-- END;
-- $$ language PLPGSQL;
--
-- CREATE FUNCTION decrement_publication_karma() RETURNS TRIGGER as
-- $$
-- BEGIN
--     UPDATE publication p
--     SET publication_karma = (SELECT COUNT(publication_id)
--                              FROM publication
--                              WHERE publication_id = NEW.publication_id) - 1
--     WHERE p.publication_id = NEW.publication_id;
--     RETURN NEW;
-- END;
-- $$ language PLPGSQL;

CREATE FUNCTION check_pub_karma_less_views()
    RETURNS TRIGGER
    LANGUAGE PLPGSQL
AS
$$
BEGIN
    IF NEW.publication_karma > NEW.publication_views_count THEN
        RAISE EXCEPTION 'Карма не может быть больше количества просмотров!';
    END IF;

    RETURN NEW;
END;
$$;


CREATE TRIGGER pub_karma_less_views_check
    AFTER INSERT OR UPDATE
    ON publication
    FOR EACH ROW
EXECUTE PROCEDURE check_pub_karma_less_views();



