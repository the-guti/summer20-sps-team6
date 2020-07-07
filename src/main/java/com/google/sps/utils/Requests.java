package com.google.sps.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to get information from HTTP Requests
 */
public class Requests {

    /**
     * Returns the value of a paramater from a HTTP request if present.
     * If the request doesn't contain a value for the requested paramater, return a default value
     */
    public static String getParameter(HttpServletRequest request, String parameterName, String defaultValue) {
        if (hasParameterValue(request, parameterName)) {
            return request.getParameter(parameterName);
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns true iff the request contains the parameter parameterName, its value is ignored
     */
    public static boolean hasParameter(HttpServletRequest request, String parameterName) {
        String parameterValue = request.getParameter(parameterName);
        return parameterValue != null;
    }

    /**
     * Returns true iff the request contains the parameterName and it has a nonempty value
     */
    public static boolean hasParameterValue(HttpServletRequest request, String parameterName) {
        return hasParameter(request, parameterName) && !"".equals(request.getParameter(parameterName));
    }
}
