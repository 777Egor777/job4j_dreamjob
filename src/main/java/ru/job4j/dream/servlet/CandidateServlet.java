package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.MemCandidateStore;
import ru.job4j.dream.store.MemPostStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Geraskin Egor
 * @version 1.0
 * @since 10.01.2021
 */
public class CandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", MemCandidateStore.instOf().findAll());
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        MemCandidateStore.instOf().save(
                new Candidate(
                        Integer.parseInt(req.getParameter("id")),
                        req.getParameter("name")
                )
        );
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
