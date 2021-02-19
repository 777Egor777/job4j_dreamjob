package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Post;
import ru.job4j.dream.store.PsqlPostStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет выполняет 2 задачи:
 * 1. Загрузка всех вакансий на JSP
 *    для дальнейшего отображения
 *    их в таблице
 *    (метод doGet).
 * 2. Получение данных с формы
 *    добавления/редактирования
 *    вакансии и внесение
 *    изменений по данной
 *    вакансии в хранилище
 *    (метод doPost).
 * @author Geraskin Egor
 * @version 1.0
 * @since 10.01.2021
 */
public class PostServlet extends HttpServlet {
    /**
     * Передача списка всех вакансий
     * на JSP с помощью атрибута
     * запроса, для дальнейшего вывода
     * их в таблицу.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("posts", PsqlPostStore.instOf().findAll());
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("posts.jsp").forward(req, resp);
    }

    /**
     * Сервлет для добавления
     * новой вакансии, или
     * изменения существующей.
     *
     * Работаем с PostgreSql хранилищем.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        PsqlPostStore.instOf().save(
                new Post(
                        Integer.parseInt(req.getParameter("id")),
                        req.getParameter("name"),
                        System.currentTimeMillis()
                )
        );
        resp.sendRedirect(req.getContextPath() + "/posts.do");
    }
}
