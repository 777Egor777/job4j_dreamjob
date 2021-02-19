package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс, описывающий
 * хранилище данных для
 * кандидатов.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 17.01.2021
 */
public interface CandidateStore {
    /**
     * Метод, возвращающий
     * всех кандидатов из хранилища.
     * @return список всех кандидатов,
     *         находящихся в хранилище
     *         на данный момент.
     */
    Collection<Candidate> findAll();

    List<Candidate> findAllByToday();

    /**
     * Метод, сохраняющий кандидата
     * в хранилище.
     * Если кандидат уже
     * был добавлен в хранилище,
     * то он будет там обновлён.
     *
     * @param candidate - кандидат.
     * @return Обновлённый объект кандидата.
     *         Если это новый кандидат,
     *         то он будет возвращён с
     *         присвоенным от хранилища
     *         id.
     */
    Candidate save(Candidate candidate);

    /**
     * Метод возвращает кандидата
     * с конкретным id.
     * @param id - id запрашиваемого
     *             кандидата
     * @return кандидат с требуемым
     *         id
     */
    Candidate findById(int id);

    /**
     * Метод возвращает кандидата
     * с конкретным именем.
     * @param name - имя запрашиваемого
     *             кандидата.
     * @return кандидат с требуемым
     *         именем.
     */
    Candidate findByName(String name);

    /**
     * Очищает хранилище,
     * удаляет из него все записи.
     */
    void clear();
}
