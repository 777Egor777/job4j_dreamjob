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
import java.util.stream.Collectors;

/**
 * Хранилище данных для кандидатов,
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
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 16.01.2021
 */
public class PsqlCandidateStore implements CandidateStore {
    private final static long MILLIS_PER_DAY =
            24L * 60L * 60L * 1000L;

    /**
     * Объект для логгирования.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PsqlCandidateStore.class.getName());

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
    private final static String TABLE_NAME = "candidate";

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

    /**
     * Создание таблицы в БД.
     */
    private void createTable() {
        String query = "create table if not exists candidate(id serial primary key, name text, photo_id int references photo(id), city_id int, created bigint);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when creating table candidate", ex);
        }
    }

    /**
     * Класс для ленивой загрузки
     * объекта внешнего класса-
     * синглтона.
     *
     * Применяется шаблон "Holder".
     */
    private final static class Holder {
        private final static CandidateStore INSTANCE = new PsqlCandidateStore();
    }

    /**
     * Метод возвращает
     * объект синглтона
     * @return - объект синглтона
     */
    public static CandidateStore instOf() {
        return Holder.INSTANCE;
    }

    /**
     * Метод, возвращающий
     * всех кандидатов из хранилища.
     * @return список всех кандидатов,
     *         находящихся в хранилище
     *         на данный момент.
     */
    @Override
    public Collection<Candidate> findAll() {
        List<Candidate> candidates = new LinkedList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from " + TABLE_NAME);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                candidates.add(new Candidate(rs.getInt("id"), rs.getString("name"),
                                             rs.getInt("photo_id"),
                                             rs.getInt("city_id"),
                                             rs.getLong("created")));
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting all items from candidate db", ex);
        }
        return candidates;
    }

    @Override
    public List<Candidate> findAllByToday() {
        return findAll().stream().filter(
                candidate -> (
                        System.currentTimeMillis() - candidate.getCreated()
                ) < MILLIS_PER_DAY
        ).collect(Collectors.toList());
    }

    /**
     * Метод, сохраняющий кандидата
     * в хранилище.
     * Если кандидат уже
     * был добавлен в хранилище,
     * то он будет там обновлён.
     *
     * @param candidate - кандидат.
     * @return Обновлённый объект кандидата.
     *         Если это новый кандидат,
     *         то он будет возвращён с
     *         присвоенным от хранилища
     *         id.
     */
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

    /**
     * Создание новой записи
     * в таблице.
     * Для объекта модели генерируется
     * и сеттится новый идентификатор.
     * @param candidate - объект модели,
     *                    который будет добавлен
     *                    в БД.
     * @return Изменённый объект модели,
     *         в поле id которого присвоен
     *         сгенерированный
     *         идентификатор.
     */
    private Candidate create(Candidate candidate) {
        String query = String.format(
                "insert into %s(name, photo_id, city_id, created) values(?,?,?, ?)",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getPhotoId());
            ps.setInt(3, candidate.getCityId());
            ps.setLong(4, candidate.getCreated());
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

    /**
     * Обновление модели кандидата.
     * Ищется запись с указанным
     * id(содержащемся в
     * соответствующем поле модели),
     * и обновляется новыми данными
     * (другими полями модели).
     *
     * @param candidate - объект модели, соответствующую
     *                    которому запись надо обновить.
     */
    private void update(Candidate candidate) {
        String query = String.format(
                "update %s set name=?, photo_id=?, city_id=?, created=? where id=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getPhotoId());
            ps.setInt(3, candidate.getCityId());
            ps.setLong(4, candidate.getCreated());
            ps.setInt(5, candidate.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOG.error("Exception when updating item in candidate db", ex);
        }
    }

    /**
     * Метод возвращает кандидата
     * с конкретным id.
     * @param id - id запрашиваемого
     *             кандидата
     * @return кандидат с требуемым
     *         id
     */
    @Override
    public Candidate findById(int id) {
        String name = "";
        int photoId = -1;
        int city_id = 0;
        long created = 0;
        String query = String.format(
                "select name, photo_id, city_id, created from %s where id=?",
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
                    created = rs.getLong("created");
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when searching item in candidate db", ex);
        }
        return new Candidate(id, name, photoId, city_id, created);
    }

    /**
     * Метод возвращает кандидата
     * с конкретным именем.
     * @param name - имя запрашиваемого
     *             кандидата.
     * @return кандидат с требуемым
     *         именем.
     */
    @Override
    public Candidate findByName(String name) {
        Candidate result = null;
        String query = String.format(
                "select id, photo_id, city_id, created from %s where name=?",
                TABLE_NAME
        );
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = new Candidate(
                            rs.getInt("id"),
                            name,
                            rs.getInt("photo_id"),
                            rs.getInt("city_id"),
                            rs.getLong("created")
                    );
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when searching item in candidate db", ex);
        }
        return result;
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
                "create table if not exists %s( id serial primary key, name text, photo_id int references photo(id), city_id int, created bigint);",
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
}
