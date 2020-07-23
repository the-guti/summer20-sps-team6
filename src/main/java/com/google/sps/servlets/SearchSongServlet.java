package com.google.sps.servlets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.sps.utils.Requests;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/search")
public class SearchSongServlet extends HttpServlet {

    /** Global instance properties filename. */
    private static String PROPERTIES_FILENAME = "youtube.properties";

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 5;

    /** Global instance of Youtube object to make all API requests. */
    private static YouTube youtube;

  // Returns comments in JSON format
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get query term from user.
    String queryTerm = Requests.getParameter(request, "q", "");

    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
        public void initialize(HttpRequest request) throws IOException {}
        }).setApplicationName("Team6-SPS-Youtube").build();

    YouTube.Search.List search = youtube.search().list("id,snippet");
    /*
    * It is important to set your API key from the Google Developer Console for
    * non-authenticated requests (found under the Credentials tab at this link:
    * console.developers.google.com/). This is good practice and increased your quota.
    */
    String apiKey = "X";
    search.setKey(apiKey);
    search.setQ(queryTerm);
    /*
    * We are only searching for videos (not playlists or channels). If we were searching for
    * more, we would add them as a string like this: "video,playlist,channel".
    */
    search.setType("video");
    /*
    * This method reduces the info returned to only the fields we need and makes calls more
    * efficient.
    */
    search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
    search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
    SearchListResponse searchResponse = search.execute();

    List<SearchResult> searchResultList = searchResponse.getItems();

    if (searchResultList != null) {
        prettyPrint(searchResultList.iterator(), queryTerm);
    }
    
    response.setContentType("application/json; charset=UTF-8;");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println("[{}]");
  }

  private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

    System.out.println("\n=============================================================");
    System.out.println(
        "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
    System.out.println("=============================================================\n");

    if (!iteratorSearchResults.hasNext()) {
      System.out.println(" There aren't any results for your query.");
    }

    while (iteratorSearchResults.hasNext()) {

      SearchResult singleVideo = iteratorSearchResults.next();
      ResourceId rId = singleVideo.getId();

      // Double checks the kind is video.
      if (rId.getKind().equals("youtube#video")) {
        Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().get("default");

        System.out.println(" Video Id" + rId.getVideoId());
        System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
        System.out.println(" Thumbnail: " + thumbnail.getUrl());
        System.out.println("\n-------------------------------------------------------------\n");
      }
    }
  }
}