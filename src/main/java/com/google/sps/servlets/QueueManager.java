package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.LinkedList;
import java.util.Queue;

@WebServlet("/queueManager")
public class QueueManager extends HttpServlet {
    private Queue<String> songQueue = new LinkedList<String>();
    private Integer currentSecond;
    private Integer startTime;
    private Integer songLength;
    private String currentSongId = "fJ9rUzIMcZQ"; // Default is "Bohemian Raphsody"
    private String songId = "dQw4w9WgXcQ"; // Default is "Never Gonna Give You Up"

    @Override
    public void init() {
        currentSecond = 0;
        // Fixed list
        songQueue.add("HgzGwKwLmgM");
        songQueue.add("a01QQZyl-_I");
        songQueue.add("azdwsXLmrHE");
        songQueue.add("f4Mc-NYPHaQ");
        songQueue.add("KXw8CRapg7k");
    }
/*
    while(songQueue.peek()){
        for(int i = startTime;i<songLength;i++){
            currentSecond++;
            // make it sleep for a second
            wait(1000);
        }
    }*/

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(songQueue.peek() != null){// Check if empty
            currentSongId = songQueue.poll();
        }
        response.setContentType("text/html;");
        response.getWriter().println(currentSongId);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        songId = request.getParameter("songId-input");
        songQueue.add(songId);
    }
}
