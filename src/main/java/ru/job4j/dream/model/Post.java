package ru.job4j.dream.model;

import net.jcip.annotations.Immutable;

import java.util.Objects;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 09.01.2021
 */
@Immutable
public final class Post extends Item {

    public Post(int id, String name) {
        super(id, name);
    }

    @Override
    public Post setId(int id) {
        return of(super.setId(id));
    }

    @Override
    public Post setName(String name) {
        return of(super.setName(name));
    }

    public static Post of(Item item) {
        return new Post(item.getId(), item.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return getId() == post.getId() && Objects.equals(getName(), post.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return String.format(
                "Post{id=%s, name=%s}",
                getId(),
                getName()
        );
    }
}
