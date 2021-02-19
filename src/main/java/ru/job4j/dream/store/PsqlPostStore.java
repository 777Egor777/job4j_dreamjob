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
import java.util.stream.Collectors;

/**
 * Хранилище данных для вакансий,
 * использующее базу данных
 * PostgreSQL
 * для хранения.
 *
 * Класс является Синглтоном - во время
 * работы приложения будет создан и будет
 * использоваться всего один
 * его объект.
 *
 * Синглтон инициализируется лениво,
 * используется шаблон "Holder"
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public final class PsqlPostStore implements PostStore {
    private final static long MILLIS_PER_DAY =
            24L * 60L * 60L * 1000L;

    /**
     * Объект для логгирования.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPostStore.class.getName());

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
     * по каждой вакансии.
     */
    private final static String TABLE_NAME = "posts";

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
        createTable();
    }

    /**
     * Создание таблицы в БД.
     */
    private void createTable() {
        String query = "create table if not exists " + TABLE_NAME + "(id serial primary key, name text, created bigint);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when creating table posts", ex);
        }
    }

    /**
     * Класс для ленивой загрузки
     * объекта внешнего класса-
     * синглтона.
     *
     * Применяется шаблон "Holder".
     */
    private static final class Lazy {
        private static final PostStore INSTANCE = new PsqlPostStore();
    }

    /**
     * Метод возвращает
     * объект синглтона
     * @return - объект синглтона
     */
    public static PostStore instOf() {
        return Lazy.INSTANCE;
    }

    /**
     * Метод, возвращающий
     * все вакансии из хранилища.
     * @return список всех вакансий,
     *         находящихся в хранилище
     *         на данный момент.
     */
    @Override
    public Collection<Post> findAll() {
        List<Post> posts = new LinkedList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from " + TABLE_NAME);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                posts.add(new Post(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getLong("created")));
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting all items from posts db", ex);
        }
        return posts;
    }

    @Override
    public List<Post> findAllByToday() {
        return findAll().stream().filter(
                post -> (
                            System.currentTimeMillis() - post.getCreated()
                        ) < MILLIS_PER_DAY
        ).collect(Collectors.toList());
    }


    /**
     * Метод, сохраняющий вакансию
     * в хранилище.
     * Если вакансия уже
     * была добавлена в хранилище,
     * то она будет там обновлена.
     *
     * @param post - вакансия.
     * @return Обновлённый объект вакансии.
     *         Если это новая вакансия,
     *         то она будет возвращена с
     *         присвоенным от хранилища
     *         id.
     */
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

    /**
     * Создание новой записи
     * в таблице.
     * Для объекта модели генерируется
     * и сеттится новый идентификатор.
     * @param post - объект модели,
     *                    который будет добавлен
     *                    в БД.
     * @return Изменённый объект модели,
     *         в поле id которого присвоен
     *         сгенерированный
     *         идентификатор.
     */
    private Post create(Post post) {
        String query = String.format(
                "insert into %s(name, created) values(?, ?)",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getName());
            ps.setLong(2, System.currentTimeMillis());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    post = post.setId(rs.getInt(1));
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when creating new item in posts db", ex);
        }
        return post;
    }

    /**
     * Обновление модели вакансии.
     * Ищется запись с указанным
     * id(содержащемся в
     * соответствующем поле модели),
     * и обновляется новыми данными
     * (другими полями модели).
     *
     * @param post - объект модели, соответствующую
     *                    которому запись надо обновить.
     */
    private void update(Post post) {
        String query = String.format(
                "update %s set name=?, created=? where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, post.getName());
            ps.setLong(2, System.currentTimeMillis());
            ps.setInt(3, post.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOG.error("Exception when updating item in posts db", ex);
        }
    }

    /**
     * Метод возвращает вакансию
     * с конкретным id.
     * @param id - id запрашиваемой
     *             вакансии.
     * @return вакансия с требуемым
     *         id.
     */
    @Override
    public Post findById(int id) {
        String name = "";
        long created = 0;
        String query = String.format(
                "select name, created from %s where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString(1);
                    created = rs.getLong(2);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when searching item in posts db by id", ex);
        }
        return new Post(id, name, created);
    }

    /**
     * Метод возвращает вакансию
     * с конкретным названием.
     * @param name - название запрашиваемой
     *             вакансии
     * @return вакансия с требуемым
     *         названием
     */
    @Override
    public Post findByName(String name) {
        int id = 0;
        long created = 0;
        String query = String.format(
                "select id, created from %s where name=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                    created = rs.getLong(2);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when searching item in posts table by name", ex);
        }
        return new Post(id, name, created);
    }

    /**
     * Очистка хранилища.
     * Таблица удаляется и создаётся
     * заново.
     */
    @Override
    public void clear() {
        String deleteQuery = String.format(
                "drop table if exists %s;",
                TABLE_NAME
        );
        String createQuery = String.format(
                "create table if not exists %s( id serial primary key, name text, created bigint);",
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
            LOG.error("Exception when clearing posts db", ex);
        }
    }
}
