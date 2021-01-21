package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 16.01.2021
 */
public class PsqlCandidateStore implements CandidateStore {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPostStore.class.getName());
    private final static int MIN_IDLE_COUNT = 5;
    private final static int MAX_IDLE_COUNT = 10;
    private final static int MAX_OPEN_PS_COUNT = 100;
    private final static String TABLE_NAME = "candidate";
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlCandidateStore() {
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
        createTable();
    }

    private void createTable() {
        String query = "create table if not exists candidate(id serial primary key, name text, photo_id int references photo(id), city_id int);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when creating table candidate", ex);
        }
    }

    private final static class Holder {
        private final static CandidateStore INSTANCE = new PsqlCandidateStore();
    }

    public static CandidateStore instOf() {
        return Holder.INSTANCE;
    }

    @Override
    public Collection<Candidate> findAll() {
        List<Candidate> candidates = new LinkedList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from " + TABLE_NAME);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                candidates.add(new Candidate(rs.getInt("id"), rs.getString("name"),
                                             rs.getInt("photo_id"),
                                             rs.getInt("city_id")));
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting all items from candidate db", ex);
        }
        return candidates;
    }

    @Override
    public Candidate save(Candidate candidate) {
        Candidate result = candidate;
        if (candidate.getId() == 0) {
            result = create(candidate);
        } else {
            update(candidate);
        }
        return result;
    }

    private Candidate create(Candidate candidate) {
        String query = String.format(
                "insert into %s(name, photo_id, city_id) values(?,?,?)",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getPhotoId());
            ps.setInt(3, candidate.getCityId());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    candidate = candidate.setId(rs.getInt(1));
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when creating new item in candidate db", ex);
        }
        return candidate;
    }

    private void update(Candidate candidate) {
        String query = String.format(
                "update %s set name=?, photo_id=?, city_id=? where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getPhotoId());
            ps.setInt(3, candidate.getCityId());
            ps.setInt(4, candidate.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOG.error("Exception when updating item in candidate db", ex);
        }
    }

    @Override
    public Candidate findById(int id) {
        String name = "";
        int photoId = -1;
        int city_id = 0;
        String query = String.format(
                "select name, photo_id, city_id from %s where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString(1);
                    photoId = rs.getInt(2);
                    city_id = rs.getInt(3);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when searching item in candidate db", ex);
        }
        return new Candidate(id, name, photoId, city_id);
    }

    @Override
    public void clear() {
        String deleteQuery = String.format(
                "drop table if exists %s;",
                TABLE_NAME
        );
        String createQuery = String.format(
                "create table if not exists %s( id serial primary key, name text, photo_id int references photo(id), city_id int);",
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
            LOG.error("Exception when clearing candidate db", ex);
        }
    }

    public static void main(String[] args) {
        Candidate c = PsqlCandidateStore.instOf().findById(1);
        System.out.println(c);
    }
}
