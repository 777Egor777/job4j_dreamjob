package ru.job4j.dream.model;

import net.jcip.annotations.Immutable;

import java.util.Objects;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 09.01.2021
 */
@Immutable
public final class Candidate extends Item {
    public Candidate(int id, String name) {
        super(id, name);
    }

    @Override
    public Candidate setId(int id) {
        return of(super.setId(id));
    }

    @Override
    public Candidate setName(String name) {
        return of(super.setName(name));
    }

    public static Candidate of(Item c) {
        return new Candidate(c.getId(), c.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return getId() == candidate.getId() && Objects.equals(getName(), candidate.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
