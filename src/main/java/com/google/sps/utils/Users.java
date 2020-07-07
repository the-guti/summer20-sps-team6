package com.google.sps.utils;

import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

// TODO move to OAuth for Users and Login/Logout
public class Users {
    public static final List<String> USER_PROPERTY_NAMES = Arrays.asList("display-name", "email");
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final UserService userService = UserServiceFactory.getUserService();

    /**
     * Queries Datastore for a User Entity with the given Id, returns null if no match
     */
    public static Entity getUserEntity(String userId) {
        Query query =
                new Query("User")
                        .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, userId));
        PreparedQuery results = datastore.prepare(query);
        return results.asSingleEntity();
    }

    public boolean isUserLoggedIn() {
        return userService.isUserLoggedIn();
    }

    /**
     * @return The current User if they are logged in, otherwise return null
     */
    public User getCurrentUser() {
        if (isUserLoggedIn()) {
            return userService.getCurrentUser();
        } else {
            return null;
        }
    }
}
