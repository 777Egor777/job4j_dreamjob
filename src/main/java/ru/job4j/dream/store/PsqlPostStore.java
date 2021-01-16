package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public final class PsqlPostStore implements PostStore {
    private final static int MIN_IDLE_COUNT = 5;
    private final static int MAX_IDLE_COUNT = 10;
    private final static int MAX_OPEN_PS_COUNT = 100;
    private final static String TABLE_NAME = "posts";
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlPostStore() {
        Properties cfg = new Properties();
        try (InputStream in =
                     PsqlPostStore.class.getClassLoader().getResourceAsStream("dp.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PsqlPostStore.instOf().clear();
        PsqlCandidateStore.instOf().clear();
    }
}
