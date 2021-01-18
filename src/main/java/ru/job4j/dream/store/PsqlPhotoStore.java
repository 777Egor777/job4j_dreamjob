package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 17.01.2021
 */
public class PsqlPhotoStore implements PhotoStore {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPostStore.class.getName());
    private final static int MIN_IDLE_COUNT = 5;
    private final static int MAX_IDLE_COUNT = 10;
    private final static int MAX_OPEN_PS_COUNT = 100;
    private final static String TABLE_NAME = "photo";
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlPhotoStore() {
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
        String query = "create table if not exists photo(id serial primary key, name text);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when creating table photo", ex);
        }
    }

    private static final class Holder {
        private final static PhotoStore INSTANCE = new PsqlPhotoStore();
    }

    public static PhotoStore instOf() {
        return Holder.INSTANCE;
    }

    @Override
    public int add(String name) {
        int result = -1;
        String query = "insert into photo(name) values(?)";
        try {
            try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.execute();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        result = rs.getInt(1);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when add item to photo table", ex);
        }
        return result;
    }

    @Override
    public void delete(int id) {
        String query = "delete from photo where id=?";
        try {
            try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement(query)) {
                ps.setInt(1, id);
                ps.execute();
            }
        } catch (Exception ex) {
            LOG.error("Exception when deleting item from photo table", ex);
        }
    }

    @Override
    public void update(int id, String newName) {
        String query = "update photo set name=? where id=?";
        try {
            try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement(query)) {
                ps.setString(1, newName);
                ps.setInt(2, id);
                ps.execute();
            }
        } catch (Exception ex) {
            LOG.error("Exception when updating item in photo table", ex);
        }
    }

    @Override
    public String get(int id) {
        String result = "";
        String query = "select name from photo where id=?";
        try {
            try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = rs.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting item from photo table", ex);
        }
        return result;
    }

    @Override
    public void clear() {
        String deleteQuery = String.format(
                "delete from %s;",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection()) {
            try (PreparedStatement psDelete = cn.prepareStatement(deleteQuery)) {
                psDelete.execute();
            }
        } catch (Exception ex) {
            LOG.error("Exception when clearing candidate db", ex);
        }
    }

    public static void main(String[] args) {
    }
}
