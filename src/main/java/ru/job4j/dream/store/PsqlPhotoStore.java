package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * Хранилище данных для фотографий
 * кандидатов,
 * использующее базу данных
 * PostgreSQL для хранения.
 *
 * Класс является Синглтоном - во время
 * работы приложения будет создан и будет
 * использоваться всего один
 * его объект.
 *
 * Синглтон инициализируется лениво,
 * используется шаблон "Holder"
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 17.01.2021
 */
public class PsqlPhotoStore implements PhotoStore {
    /**
     * Объект для логгирования.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPhotoStore.class.getName());

    /**
     * Настройки пула соединений
     * с БД.
     */
    private final static int MIN_IDLE_COUNT = 5;
    private final static int MAX_IDLE_COUNT = 10;
    private final static int MAX_OPEN_PS_COUNT = 100;

    /**
     * Имя таблицы в БД,
     * в которой хранятся данные
     * по каждому кандидату.
     */
    private final static String TABLE_NAME = "photo";

    /**
     * Пул соединений с Базой Данных.
     */
    private final BasicDataSource pool = new BasicDataSource();

    /**
     * В классе только один приватный
     * конструктор - так как это
     * синглтон.
     *
     * В конструкторе инициализируется
     * и настраивается пул соединений
     * с БД.
     */
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

    /**
     * Создание таблицы в БД.
     */
    private void createTable() {
        String query = "create table if not exists photo(id serial primary key, name text);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when creating table photo", ex);
        }
    }

    /**
     * Класс для ленивой загрузки
     * объекта внешнего класса-
     * синглтона.
     *
     * Применяется шаблон "Holder".
     */
    private static final class Holder {
        private final static PhotoStore INSTANCE = new PsqlPhotoStore();
    }

    /**
     * Метод возвращает
     * объект синглтона
     * @return - объект синглтона
     */
    public static PhotoStore instOf() {
        return Holder.INSTANCE;
    }

    /**
     * Добавить фото с
     * данным именем.
     *
     * @param name - имя фото.
     * @return сгенерированный
     *         хранилищем id
     *         для фото.
     */
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

    /**
     * Удалить фото с данным id
     * из хранилища.
     *
     * @param id - идентификатор фото,
     *             которое надо удалить.
     */
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

    /**
     * Обновиться запись с
     * данным id в хранилище,
     * изменить название
     * фотографии, соответствующее
     * данному id.
     *
     * @param id - идентификатор записи,
     *             название фото в которой
     *             надо поменять.
     * @param newName - новое имя фотографии
     *                  у записи с
     *                  идентификатором id.
     */
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

    /**
     * Вернуть название фотографии
     * с данным id.
     * @param id - идентификатор требуемой
     *             фотографии
     * @return название фотографии
     */
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

    /**
     * Очистить хранилище.
     */
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
}
