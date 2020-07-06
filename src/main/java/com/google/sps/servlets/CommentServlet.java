package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.ArrayList;
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
    String partyId = request.getParameter("id");

    response.setContentType("application/json; charset=UTF-8;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println("Quiubo perros, estos son los comentarios" + partyId);
  }

  // Takes incoming comments and adds them to list
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect("/index.html");
  }

    /**
    * Converts an ArrayList into a JSON string using the Gson library.
    */
    private String convertToJson(ArrayList arr) {
      Gson gson = new Gson();
      String json = gson.toJson(arr);
      return json;
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