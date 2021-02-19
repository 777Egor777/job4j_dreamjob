package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.MemCityStore;
import ru.job4j.dream.store.PsqlCandidateStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет выполняет 2 задачи:
 * 1. Загрузка всех кандидатов на JSP
 *    для дальнейшего отображения
 *    их в таблице
 *    (метод doGet).
 * 2. Получение данных с формы
 *    добавления/редактирования
 *    кандидата и внесение
 *    изменений по данному
 *    кандидату в хранилище
 *    (метод doPost).
 * @author Geraskin Egor
 * @version 1.0
 * @since 10.01.2021
 */
public class CandidateServlet extends HttpServlet {
    /**
     * Передача списка всех кандидатов
     * на JSP с помощью атрибута
     * запроса, для дальнейшего вывода
     * их в таблицу.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", PsqlCandidateStore.instOf().findAll());
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }

    /**
     * Сервлет для добавления
     * нового кандидата, или
     * изменения существующего.
     *
     * Получение данных с формы JSP.
     * Определение, что сейчас делаем -
     * добавляем новую запись(id == 0),
     * или обновляем существующую
     * (id != 0).
     *
     * Работаем с PostgreSql хранилищем.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));
        Candidate candidate = new Candidate(0, "", 1, 0);
        if (id != 0) {
            candidate = PsqlCandidateStore.instOf().findById(id);
        }
        candidate = candidate.setName(req.getParameter("name"));
        candidate = candidate.setCityId(MemCityStore.instOf().getIdByName(req.getParameter("city")));
        PsqlCandidateStore.instOf().save(
                candidate
        );
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
