package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.utils.Requests;
import com.google.sps.utils.Users;

/**
 * Uses UserService and DataStore to allow Creation, Reading, and Updating of User objects
 * JSON is sent on GET, on POST create a new user object or update individual parameters
 */
@WebServlet("/userInfo")
public class UserInformationServlet extends HttpServlet {
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final UserService userService = UserServiceFactory.getUserService();
    private static Gson gson = new Gson();

    /**
     * Sends JSON representation of the User with id user-id
     * If user-id is not present, send the logged in user's id
     * If no user-id is given and no user is logged in, return a HTTP 400 error code
     * If no data is found for the given user, return a HTTP 404 error code
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String currentUserId = null;
        if (userService.isUserLoggedIn()) {
            currentUserId = userService.getCurrentUser().getUserId();
        }
        String requestedUserId = Requests.getParameter(request, "user-id", currentUserId);
        if (requestedUserId == null) {
            response.setStatus(400);
            return;
        }
        Entity requestedUserEntity = Users.getUserEntity(requestedUserId);
        if (requestedUserEntity == null) {
            response.setStatus(404);
            return;
        }
        String requestedUserJson = gson.toJson(requestedUserEntity.getProperties());
        response.setContentType("application/json;");
        response.getWriter().println(requestedUserJson);
    }


    /**
     * Deletes User Information for the logged in user
     * If no user logged in, return a HTTP 400 error code
     * If no User Information present, return a HTTP 404 error code
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.isUserLoggedIn()) {
            response.setStatus(400);
            return;
        }
        String userId = userService.getCurrentUser().getUserId();
        Entity userEntity = Users.getUserEntity(userId);
        if (userEntity != null) {
            datastore.delete(userEntity.getKey());
        } else {
            response.setStatus(404);
        }
    }

    /**
     * Creates or Updates the current user's User Object with parameters given
     * If User isn't logged in return a HTTP 400 error code, Datastore is not modified
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!userService.isUserLoggedIn()) {
            response.setStatus(400);
            response.sendRedirect("/");
            return;
        }
        String userId = userService.getCurrentUser().getUserId();
        Entity userEntity = Users.getUserEntity(userId);

        if (userEntity == null) {
            // Create and store a new entity
            userEntity = new Entity("User", userId);
            String userEmail = userService.getCurrentUser().getEmail();
            for (String propertyName : Users.USER_PROPERTY_NAMES) {
                String propertyValue = Requests.getParameter(request, propertyName, "");
                userEntity.setProperty(propertyName, propertyValue);
            }
            userEntity.setProperty("id", userId);
            userEntity.setProperty("email", userEmail); // email shouldn't be changed on create
        } else {
            // Update present parameters
            for (String propertyName : Users.USER_PROPERTY_NAMES) {
                String currentPropertyValue = "";
                if (userEntity.hasProperty(propertyName)) {
                    currentPropertyValue = (String) userEntity.getProperty(propertyName);
                }
                String updatedPropertyValue = Requests.getParameter(request, propertyName, currentPropertyValue);
                userEntity.setProperty(propertyName, updatedPropertyValue);
            }
        }
        datastore.put(userEntity);
        response.sendRedirect("/");
    }
}
