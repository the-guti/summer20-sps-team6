package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/musicControler")
public class MusicControlerServlet extends HttpServlet {
    private int startTime;

    @Override
    public void init() {
        startTime = 50;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO: Get party id, look in DB for parameters of party, get current start time

        response.setContentType("text/html;");
        response.getWriter().println(startTime);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        startTime = Integer.parseInt(request.getParameter("start-time-input"));
    }
}
