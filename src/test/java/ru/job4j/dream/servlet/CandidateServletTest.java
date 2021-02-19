package ru.job4j.dream.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlCandidateStore.class)
public class CandidateServletTest {

    @Test
    public void whenAddNewCandidate() {
        CandidateStore store = MemCandidateStore.instOf();
        PowerMockito.mockStatic(PsqlCandidateStore.class);
        Mockito.when(PsqlCandidateStore.instOf()).thenReturn(store);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("id")).thenReturn("0");
        Mockito.when(req.getParameter("name")).thenReturn("Test");
        Mockito.when(req.getParameter("city")).thenReturn("Saratov");
        try {
            new CandidateServlet().doPost(req, resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Candidate c = store.findByName("Test");
        assertTrue(c.getId() != 0);
    }

    @Test
    public void whenEditCandidate() {
        CandidateStore store = MemCandidateStore.instOf();
        PowerMockito.mockStatic(PsqlCandidateStore.class);
        Mockito.when(PsqlCandidateStore.instOf()).thenReturn(store);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("id")).thenReturn("1");
        Mockito.when(req.getParameter("name")).thenReturn("Test");
        Mockito.when(req.getParameter("city")).thenReturn("Saratov");
        try {
            new CandidateServlet().doPost(req, resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Candidate c = store.findById(1);
        assertThat(c.getName(), is("Test"));
        assertThat(MemCityStore.instOf().getNameById(c.getCityId()), is("Saratov"));
    }
}