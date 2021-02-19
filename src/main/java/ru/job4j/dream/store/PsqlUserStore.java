package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.User;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Хранилище данных для пользователей,
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

 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public final class PsqlUserStore implements UserStore {
    /**
     * Объект для логгирования.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PsqlUserStore.class.getName());

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
     * по каждому пользователю.
     */
    private final static String TABLE_NAME = "users";

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
    private PsqlUserStore() {
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
        String query = "create table if not exists \"users\"(id serial primary key, name text, email text, password text);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when creating table user", ex);
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
        private final static UserStore INSTANCE = new PsqlUserStore();
    }

    /**
     * Метод возвращает
     * объект синглтона
     * @return - объект синглтона
     */
    public static UserStore instOf() {
        return Holder.INSTANCE;
    }

    /**
     * Метод, сохраняющий пользователя
     * в хранилище.
     * Если пользователь уже
     * был добавлен в хранилище,
     * то он будет там обновлён.
     *
     * @param user - пользователь.
     * @return Обновлённый объект пользователя.
     *         Если это новый пользователь,
     *         то он будет возвращён с
     *         присвоенным от хранилища
     *         id.
     */
    @Override
    public int save(User user) {
        int newId = user.getId();
        if (newId == -1) {
            newId = create(user);
        } else {
            update(user);
        }
        return newId;
    }

    /**
     * Создание новой записи
     * в таблице.
     * Для объекта модели генерируется
     * и сеттится новый идентификатор.
     * @param user - объект модели,
     *                    который будет добавлен
     *                    в БД.
     * @return Изменённый объект модели,
     *         в поле id которого присвоен
     *         сгенерированный
     *         идентификатор.
     */
    private int create(User user) {
        int idResult = -1;
        String query = "insert into \"users\"(name, email, password) values(?,?,?);";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idResult = rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when inserting into user db", ex);
        }
        return idResult;
    }

    /**
     * Обновление модели пользователя.
     * Ищется запись с указанным
     * id(содержащемся в
     * соответствующем поле модели),
     * и обновляется новыми данными
     * (другими полями модели).
     *
     * @param user - объект модели, соответствующую
     *                    которому запись надо обновить.
     */
    private void update(User user) {
        String query = "update \"users\" set name=?, email=?, password=? where id=?;";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOG.error("Exception when updating user db", ex);
        }
    }

    /**
     * Метод возвращает пользователя
     * с конкретным id.
     * @param id - id запрашиваемого
     *             пользователя.
     * @return пользователь с требуемым
     *         id.
     */
    @Override
    public User findById(int id) {
        User result = new User();
        result = result.setId(id);
        String query = "select * from \"users\" where id=?;";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = result.setEmail(rs.getString("email"));
                    result = result.setName(rs.getString("name"));
                    result = result.setPassword(rs.getString("password"));
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting from table by id", ex);
        }
        return result;
    }

    /**
     * Метод возвращает пользователя
     * с конкретным email.
     * @param email - email запрашиваемого
     *             пользователя.
     * @return пользователь с требуемым
     *         email.
     */
    @Override
    public User findByEmail(String email) {
        User result = new User();
        result = result.setEmail(email);
        String query = "select * from \"users\" where email=?;";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = result.setId(rs.getInt("id"));
                    result = result.setName(rs.getString("name"));
                    result = result.setPassword(rs.getString("password"));
                }
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting from table by email", ex);
        }
        return result;
    }

    /**
     * Метод возвращает всех
     * пользователей из
     * хранилища.
     *
     * @return Список всех зарегистрированных
     *         на данный момент в сервисе
     *         пользователей.
     */
    @Override
    public List<User> getAll() {
        List<User> result = new ArrayList<>();
        String query = "select * from \"users\";";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        } catch (Exception ex) {
            LOG.error("Exception when extracting all items from table", ex);
        }
        return result;
    }

    /**
     * Очищает хранилище,
     * удаляет из него все записи.
     */
    @Override
    public void clear() {
        String query = "delete from \"users\";";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.execute();
        } catch (Exception ex) {
            LOG.error("Exception when deleting all items from table", ex);
        }
    }
}
