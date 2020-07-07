package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Uses UserService to login the current user and foward them to a desired page
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final UserService userService = UserServiceFactory.getUserService();

    /**
     * Redirects to a login page which forwards the user to the destination-url paramater after login
     * If destination-url is not given as a paramater its value defaults to the index
     * If the user is already logged in then redirect the user directly to the destination-url
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String destinationURL = getDestinationUrl(request);
        String redirectURL;
        if (userService.isUserLoggedIn()) {
            redirectURL = destinationURL;
        } else {
            redirectURL = userService.createLoginURL(destinationURL);
        }
        response.sendRedirect(redirectURL);
    }

    private String getDestinationUrl(HttpServletRequest request) {
        String destinationURL = request.getParameter("destination-url");
        if (destinationURL == null) {
            return "/";
        } else {
            return destinationURL;
        }
    }
}
