package ru.job4j.dream.model;

import net.jcip.annotations.Immutable;

import java.util.Objects;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 09.01.2021
 */
@Immutable
public final class Candidate {
    private final int id;
    private final String name;
    private final int photoId;
    private final int cityId;

    public Candidate(int id, String name, int cityId) {
        this.id = id;
        this.name = name;
        photoId = 1;
        this.cityId = cityId;
    }

    public Candidate(int id, String name, int photo_id, int cityId) {
        this.id = id;
        this.name = name;
        this.photoId = photo_id;
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPhotoId() {
        return photoId;
    }

    public int getCityId() {
        return cityId;
    }

    public Candidate setId(int id) {
        return new Candidate(id, name, photoId, cityId);
    }

    public Candidate setName(String name) {
        return new Candidate(id, name, photoId, cityId);
    }

    public Candidate setPhotoId(int photoId) {
        return new Candidate(id, name, photoId, cityId);
    }

    public Candidate setCityId(int cityId) {
        return new Candidate(id, name, photoId, cityId);
    }

    public static Candidate of(Candidate c) {
        return new Candidate(c.id, c.name, c.photoId, c.cityId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return id == candidate.id && Objects.equals(name, candidate.name)
                && photoId == candidate.photoId
                && cityId == candidate.cityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, photoId, cityId);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photoId=" + photoId +
                ", cityId=" + cityId +
                '}';
    }
}
