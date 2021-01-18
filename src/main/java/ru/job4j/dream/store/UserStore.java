package ru.job4j.dream.store;

import ru.job4j.dream.model.User;

import java.util.List;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public interface UserStore {
    int save(User user);
    User findById(int id);
    User findByEmail(String email);
    List<User> getAll();
    void clear();
}
