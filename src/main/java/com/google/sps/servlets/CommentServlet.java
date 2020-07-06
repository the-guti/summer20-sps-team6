package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comment")
public class CommentServlet extends HttpServlet {

  // Returns comments in JSON format
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String requestId = getParameter(request, "id", "");

    Query query = new Query("partyComment").addSort("timestampMs", SortDirection.ASCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
        String text = (String) entity.getProperty("text");
        String name = (String) entity.getProperty("name");
        String partyId = (String) entity.getProperty("partyId");
        long timestamp = (long) entity.getProperty("timestampMs");
        if(requestId.equals(partyId)){
            Comment comment = new Comment(partyId, text, name, timestamp);
            comments.add(comment);
        }

    }

    Gson gson = new Gson();

    response.setContentType("application/json; charset=UTF-8;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(gson.toJson(comments));
  }

  // Takes incoming comments and adds them to list
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = getParameter(request, "text-comment", "");
    String partyId = getParameter(request, "party-id", "");
    String name = getParameter(request, "name-comment", "");

    // Add new comment only if the strings are more than just white spaces
    if(text.trim().length() > 0 && partyId.trim().length() > 0 && name.trim().length() > 0){
      long timestampMs = System.currentTimeMillis();

      Entity taskEntity = new Entity("partyComment");
      taskEntity.setProperty("text", text);
      taskEntity.setProperty("timestampMs", timestampMs);
      taskEntity.setProperty("name", name);
      taskEntity.setProperty("partyId", partyId);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);
    }

    response.sendRedirect("/party.html?id=" + partyId);
  }

    /**
    * Get parameter value in HTTP request.
    */
    private String getParameter(HttpServletRequest request, String name, String defaultValue) {
      String value = request.getParameter(name);
      if (value == null) {
          return defaultValue;
        }
      return value;
    }
}