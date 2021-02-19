package ru.job4j.dream.store;

import ru.job4j.dream.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 17.02.2021
 */
public class MemUserStore implements UserStore {
    private final Map<Integer, User> store = new HashMap<>();
    private int id = 0;

    @Override
    public int save(User user) {
        if (user.getId() == -1) {
            id++;
            user.setId(id);
        }
        store.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public User findById(int id) {
        return store.get(id);
    }

    @Override
    public User findByEmail(String email) {
        User result = null;
        for (User user : store.values()) {
            if (user.getEmail().equals(email)) {
                result = user;
            }
        }
        return result;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void clear() {

    }
}
