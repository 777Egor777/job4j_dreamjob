package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public interface PostStore {
    Collection<Post> findAll();
    Post save(Post post);
    Post findById(int id);
    Post findByName(String name);
    void clear();
}
