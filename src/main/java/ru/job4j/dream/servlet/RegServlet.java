package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.PsqlUserStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Сервлет для страницы регистрации
 * пользователей.
 *
 * Поддерживает валидацию, если
 * введённый e-mail уже занят,
 * пользователь будет перенаправлен
 * на соответвующую страницу с указанием
 * ошибки.
 *
 * @author Egor Geraskin(yegeraskin13@gmail.com)
 * @version 1.0
 * @since 18.01.2021
 */
public class RegServlet extends HttpServlet {
    /**
     * Переопределяет метод doGet
     * класса HttpServlet.
     *
     * Перенаправляет запрос на JSP
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("reg.jsp").forward(req, resp);
    }

    /**
     * Переопределяет метод doPost
     * класса HttpServlet
     *
     * Получает данные пользователя
     * с формы JSP.
     * Валидирует их.
     *
     * Добавляет нового пользователя
     * в хранилище.
     *
     * Добавляет объект пользователя
     * в сессию.
     *
     * @param req - объект запроса(HttpServletRequest)
     * @param resp - объект ответа(HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (PsqlUserStore.instOf().findByEmail(email).getId() != -1) {
            req.getRequestDispatcher("reg_email_already_exists.jsp").forward(req, resp);
        } else {
            User user = new User(name, email, password);
            user = user.setId(PsqlUserStore.instOf().save(user));
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/index.do");
        }
    }
}
