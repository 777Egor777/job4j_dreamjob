package ru.job4j.dream.model;

import net.jcip.annotations.Immutable;

import java.util.Objects;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 09.01.2021
 */
@Immutable
public final class Post {
    private final int id;
    private final String name;

    public Post(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Post setId(int id) {
        return new Post(id, name);
    }

    public Post setName(String name) {
        return new Post(id, name);
    }

    public static Post of(Post post) {
        return new Post(post.id, post.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && Objects.equals(name, post.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return String.format(
                "Post{id=%s, name=%s}",
                id,
                name
        );
    }
}
