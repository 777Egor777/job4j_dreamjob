package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс, описывающий
 * хранилище данных для
 * вакансий.
 *
 * @author Geraskin Egor
 * @version 1.0
 * @since 11.01.2021
 */
public interface PostStore {
    /**
     * Метод, возвращающий
     * все вакансии из хранилища.
     * @return коллекция всех вакансий,
     *         находящихся в хранилище
     *         на данный момент.
     */
    Collection<Post> findAll();

    List<Post> findAllByToday();

    /**
     * Метод, сохраняющий вакансию
     * в хранилище.
     * Если вакансия уже
     * была добавлена в хранилище,
     * то она будет там обновлена.
     *
     * @param post - вакансия.
     * @return Обновлённый объект вакансии.
     *         Если это новая вакансия,
     *         то она будет возвращена с
     *         присвоенным от хранилища
     *         id.
     */
    Post save(Post post);

    /**
     * Метод возвращает вакансию
     * с конкретным id.
     * @param id - id запрашиваемой
     *             вакансии
     * @return вакансия с требуемым
     *         id
     */
    Post findById(int id);

    /**
     * Метод возвращает вакансию
     * с конкретным названием.
     * @param name - название запрашиваемой
     *             вакансии
     * @return вакансия с требуемым
     *         названием
     */
    Post findByName(String name);

    /**
     * Очищает хранилище,
     * удаляет из него все записи.
     */
    void clear();
}
