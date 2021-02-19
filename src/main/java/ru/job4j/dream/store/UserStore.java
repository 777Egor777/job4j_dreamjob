package ru.job4j.dream.store;

import ru.job4j.dream.model.User;

import java.util.List;

/**
 * Интерфейс, описывающий
 * хранилище данных для
 * пользователей приложения.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public interface UserStore {
    /**
     * Метод, сохраняющий пользователя
     * в хранилище.
     * Если пользователь уже
     * был добавлен в хранилище,
     * то он будет там обновлен.
     *
     * @param user - пользователь
     * @return id пользователя
     */
    int save(User user);

    /**
     * Метод возвращает пользователя
     * с конкретным id.
     * @param id - id запрашиваемого
     *             пользователя.
     * @return пользователь с требуемым
     *         id.
     */
    User findById(int id);

    /**
     * Метод возвращает пользователя
     * с конкретным email.
     * @param email - email запрашиваемого
     *             пользователя.
     * @return пользователь с требуемым
     *         email.
     */
    User findByEmail(String email);

    /**
     * Метод возвращает всех
     * пользователей из
     * хранилища.
     *
     * @return Список всех зарегистрированных
     *         на данный момент в сервисе
     *         пользователей.
     */
    List<User> getAll();

    /**
     * Очищает хранилище,
     * удаляет из него все записи.
     */
    void clear();
}
