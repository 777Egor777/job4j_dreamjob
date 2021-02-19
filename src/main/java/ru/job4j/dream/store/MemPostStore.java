package ru.job4j.dream.store;

import net.jcip.annotations.ThreadSafe;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Хранилище данных для вакансий,
 * использующее оперативную память
 * для хранения.
 *
 * Класс является Синглтоном - во время
 * работы приложения будет создан и будет
 * использоваться всего один
 * его объект.
 *
 * Синглтон инициализируется лениво,
 * используется шаблон "Holder"
 *
 * @author Geraskin Egor(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 09.01.2021
 */
@ThreadSafe
public final class MemPostStore implements PostStore {
    private final static long MILLIS_PER_DAY =
            24L * 60L * 60L * 1000L;

    /**
     * Карта, где хранятся все вакансии.
     */
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();

    /**
     * Инкрементируемый id
     * новой вакансии.
     */
    private static final AtomicInteger POST_ID = new AtomicInteger(4);

    /**
     * Класс для ленивой загрузки
     * объекта внешнего класса-
     * синглтона.
     *
     * Применяется шаблон "Holder".
     */
    private static final class Holder {
        private final static PostStore INSTANCE = new MemPostStore();
    }

    /**
     * В классе только один приватный
     * конструктор - так как это
     * синглтон.
     *
     * В конструкторе добавляются
     * все необходимые вакансии
     * в список.
     */
    private MemPostStore() {
        posts.put(1, new Post(1, "Junior Java Job", System.currentTimeMillis()));
        posts.put(2, new Post(2, "Middle Java Job", System.currentTimeMillis()));
        posts.put(3, new Post(3, "Senior Java Job", System.currentTimeMillis()));
    }

    /**
     * Метод возвращает
     * объект синглтона
     * @return - объект синглтона
     */
    public static PostStore instOf() {
        return Holder.INSTANCE;
    }

    /**
     * Метод, возвращающий
     * все вакансии из хранилища.
     * @return коллекция всех вакансий,
     *         находящихся в хранилище
     *         на данный момент.
     */
    @Override
    public Collection<Post> findAll() {
        return posts.values();
    }

    @Override
    public List<Post> findAllByToday() {
        return findAll().stream().filter(
                post -> (
                        System.currentTimeMillis() - post.getCreated()
                ) < MILLIS_PER_DAY
        ).collect(Collectors.toList());
    }

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
    @Override
    public Post save(Post post) {
        if (post.getId() == 0) {
            post = post.setId(POST_ID.getAndIncrement());
        }
        posts.put(post.getId(), post);
        return post;
    }

    /**
     * Метод возвращает вакансию
     * с конкретным id.
     * @param id - id запрашиваемой
     *             вакансии
     * @return вакансия с требуемым
     *         id
     */
    @Override
    public Post findById(int id) {
        return posts.get(id);
    }

    /**
     * Метод возвращает вакансию
     * с конкретным названием.
     * @param name - название запрашиваемой
     *             вакансии
     * @return вакансия с требуемым
     *         названием
     */
    @Override
    public Post findByName(String name) {
        Post result = null;
        for (Post post : posts.values()) {
            if (post.getName().equals(name)) {
                result = post;
                break;
            }
        }
        return  result;
    }

    /**
     * Очищает хранилище,
     * удаляет из него все записи.
     */
    @Override
    public void clear() {
        posts.clear();
    }
}
