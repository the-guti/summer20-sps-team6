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
@WebServlet("/create")
public class CreatePartyServlet extends HttpServlet {

  // Returns comments in JSON format
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String partyName = request.getParameter("name");
    long partyId = -1;
    if(partyName.trim().length() > 0){
      long timestampMs = System.currentTimeMillis();

      Entity taskEntity = new Entity("party");
      taskEntity.setProperty("name", partyName);
      taskEntity.setProperty("timestampMs", timestampMs);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      partyId = datastore.put(taskEntity).getId();
    }

    response.setContentType("application/json; charset=UTF-8;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println("{\"id\": " + Long.toString(partyId) + ", \"partyName\": \"" + partyName + "\"}" );
  }
}