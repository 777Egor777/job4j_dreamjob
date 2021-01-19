package ru.job4j.dream.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.store.MemPostStore;
import ru.job4j.dream.store.PostStore;
import ru.job4j.dream.store.PsqlPostStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PsqlPostStore.class)
public class PostServletTest {

    @Test
    public void whenAddNewPost() {
        PostStore store = MemPostStore.instOf();
        PowerMockito.mockStatic(PsqlPostStore.class);
        Mockito.when(PsqlPostStore.instOf()).thenReturn(store);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("id")).thenReturn("0");
        Mockito.when(req.getParameter("name")).thenReturn("Test");
        try {
            new PostServlet().doPost(req, resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(MemPostStore.instOf().findByName("Test").getId() != 0);
    }

    @Test
    public void whenUpdateExistPost() {
        PostStore store = MemPostStore.instOf();
        PowerMockito.mockStatic(PsqlPostStore.class);
        Mockito.when(PsqlPostStore.instOf()).thenReturn(store);
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getParameter("id")).thenReturn("1");
        Mockito.when(req.getParameter("name")).thenReturn("Test");
        try {
            new PostServlet().doPost(req, resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(MemPostStore.instOf().findById(1), is(new Post(1, "Test")));
    }
}