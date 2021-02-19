package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Хранилище данных для кандидатов,
 * использующее оперативную память
 * для хранения.
 *
 * Класс является Синглтоном - во время
 * работы приложения будет создан и будет
 * использоваться всего один
 * его объект.
 *
 * Синглтон инициализируется сразу(не лениво),
 * в final-static поле.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 16.01.2021
 */
public class MemCandidateStore implements CandidateStore {
    private final static long MILLIS_PER_DAY =
            24L * 60L * 60L * 1000L;

    /**
     * Единственный создаваемый объект класса.
     */
    private final static MemCandidateStore INSTANCE = new MemCandidateStore();

    /**
     * Карта, где хранятся все кандидаты.
     */
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    /**
     * Инкрементируемый id
     * нового кандидата.
     */
    private static final AtomicInteger CANDIDATE_ID = new AtomicInteger(11);

    /**
     * Конструктор только приватный,
     * так как это Синглтон.
     */
    private MemCandidateStore() {
        candidates.put(1, new Candidate(1, "Junior Java", 0, System.currentTimeMillis()));
        candidates.put(2, new Candidate(2, "Middle Java", 0, System.currentTimeMillis()));
        candidates.put(3, new Candidate(3, "Senior Java", 0, System.currentTimeMillis()));
    }

    /**
     * Метод, который возвращает
     * объект синглтона
     * @return - синглтон-объект данного класса
     */
    public static CandidateStore instOf() {
        return INSTANCE;
    }

    /**
     * Метод, возвращающий
     * всех кандидатов из хранилища.
     * @return список всех кандидатов,
     *         находящихся в хранилище
     *         на данный момент.
     */
    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

    @Override
    public List<Candidate> findAllByToday() {
        return findAll().stream().filter(
                candidate -> (
                        System.currentTimeMillis() - candidate.getCreated()
                ) < MILLIS_PER_DAY
        ).collect(Collectors.toList());
    }

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
    @Override
    public Candidate save(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate = candidate.setId(CANDIDATE_ID.incrementAndGet());
            candidate = candidate.setCreated(System.currentTimeMillis());
        }
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    /**
     * Метод возвращает кандидата
     * с конкретным id.
     * @param id - id запрашиваемого
     *             кандидата
     * @return кандидат с требуемым
     *         id
     */
    @Override
    public Candidate findById(int id) {
        return candidates.get(id);
    }

    /**
     * Метод возвращает кандидата
     * с конкретным именем.
     * @param name - имя запрашиваемого
     *             кандидата.
     * @return кандидат с требуемым
     *         именем.
     */
    @Override
    public Candidate findByName(String name) {
        Candidate result = null;
        for (Candidate c : candidates.values()) {
            if (c.getName().equals(name)) {
                result = c;
                break;
            }
        }
        return result;
    }

    /**
     * Очищает хранилище,
     * удаляет из него все записи.
     */
    @Override
    public void clear() {
        candidates.clear();
    }
}
