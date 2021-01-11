package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Item;
import ru.job4j.dream.model.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
public final class PsqlStore implements Store {
    private final static int MIN_IDLE_COUNT = 5;
    private final static int MAX_IDLE_COUNT = 10;
    private final static int MAX_OPEN_PS_COUNT = 100;
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (InputStream in =
                     PsqlStore.class.getClassLoader().getResourceAsStream("dp.properties")) {
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
        private static final Store INSTANCE = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INSTANCE;
    }

    private Collection<Item> findAllByTableName(String tableName) {
        List<Item> items = new LinkedList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from " + tableName);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(new Item(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return items;
    }

    @Override
    public Collection<Post> findAllPosts() {
        return findAllByTableName("post")
                .stream()
                .map(Post::of)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        return findAllByTableName("candidate")
                .stream()
                .map(Candidate::of)
                .collect(Collectors.toList());
    }

    @Override
    public Post save(Post post) {
        return Post.of(save(post, "post"));
    }

    @Override
    public Candidate save(Candidate candidate) {
        return Candidate.of(save(candidate, "candidate"));
    }

    private Item save(Item item, String table) {
        Item result = item;
        if (item.getId() == 0) {
            result = create(item, table);
        } else {
            update(item, table);
        }
        return result;
    }

    private Item create(Item item, String table) {
        String query = String.format(
                "insert into %s(name) values(?)",
                table
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item = item.setId(rs.getInt(1));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return item;
    }

    private void update(Item item, String table) {
        String query = String.format(
                "update %s set name=? where id=?",
                table
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, item.getName());
            ps.setInt(2, item.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Post findPostById(int id) {
        Item item = findItemById(id, "post");
        return Post.of(item);
    }

    @Override
    public Candidate findCandidateById(int id) {
        Item item = findItemById(id, "candidate");
        return Candidate.of(item);
    }

    private void clearTable(String table) {
        String query = String.format(
                "delete from %s",
                table
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void clear() {
        clearTable("post");
        clearTable("candidate");
    }

    private Item findItemById(int id, String table) {
        String name = "";
        String query = String.format(
                "select name from %s where id=?",
                table
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
        return new Item(id, name);
    }
}
