package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Post;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public final class PsqlPostStore implements PostStore {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPostStore.class.getName());
    private final static int MIN_IDLE_COUNT = 5;
    private final static int MAX_IDLE_COUNT = 10;
    private final static int MAX_OPEN_PS_COUNT = 100;
    private final static String TABLE_NAME = "posts";
    private final BasicDataSource pool = new BasicDataSource();


    private PsqlPostStore() {
        Properties cfg = new Properties();
        try (FileReader reader = new FileReader("dp.properties")) {
            cfg.load(reader);
        } catch (IOException e) {
            LOG.error("Exception when loading db.properties cfg", e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            LOG.error("Exception when registering JDBC driver", e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(MIN_IDLE_COUNT);
        pool.setMaxIdle(MAX_IDLE_COUNT);
        pool.setMaxOpenPreparedStatements(MAX_OPEN_PS_COUNT);
    }

    private static final class Lazy {
        private static final PostStore INSTANCE = new PsqlPostStore();
    }

    public static PostStore instOf() {
        return Lazy.INSTANCE;
    }

    @Override
    public Collection<Post> findAll() {
        List<Post> posts = new LinkedList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from " + TABLE_NAME);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                posts.add(new Post(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting all items from post db", ex);
        }
        return posts;
    }

    @Override
    public Post save(Post post) {
        Post result = post;
        if (post.getId() == 0) {
            result = create(post);
        } else {
            update(post);
        }
        return result;
    }

    private Post create(Post post) {
        String query = String.format(
                "insert into %s(name) values(?)",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post = post.setId(rs.getInt(1));
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when creating new item in post db", ex);
        }
        return post;
    }

    private void update(Post post) {
        String query = String.format(
                "update %s set name=? where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, post.getName());
            ps.setInt(2, post.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOG.error("Exception when updating item in post db", ex);
        }
    }

    @Override
    public Post findById(int id) {
        String name = "";
        String query = String.format(
                "select name from %s where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString(1);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when searching item in post db", ex);
        }
        return new Post(id, name);
    }

    @Override
    public void clear() {
        String deleteQuery = String.format(
                "drop table if exists %s;",
                TABLE_NAME
        );
        String createQuery = String.format(
                "create table if not exists %s( id serial primary key, name text );",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection()) {
            try (PreparedStatement psDelete = cn.prepareStatement(deleteQuery)) {
                psDelete.execute();
            }
            Thread.sleep(100);
            try (PreparedStatement psCreate = cn.prepareStatement(createQuery)) {
                psCreate.execute();
            }
        } catch (Exception ex) {
            LOG.error("Exception when clearing post db", ex);
        }
    }
}
