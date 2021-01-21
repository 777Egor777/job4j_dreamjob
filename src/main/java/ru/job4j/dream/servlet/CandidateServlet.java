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
 * @author Geraskin Egor
 * @version 1.0
 * @since 10.01.2021
 */
public class CandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", PsqlCandidateStore.instOf().findAll());
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }

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
