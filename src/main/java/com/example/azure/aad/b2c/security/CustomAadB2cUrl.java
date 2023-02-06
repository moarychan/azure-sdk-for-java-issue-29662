package com.example.azure.aad.b2c.security;

public class CustomAadB2cUrl {
    private static final String END_SESSION_URL_WITHOUT_REDIRECT_URI_PATTERN = "oauth2/v2.0/logout?p=%s";

    /**
     * Gets the end session URL.
     * @param baseUri the base URI.
     * @param userFlow the user flow instance id
     * @return the end session URL
     */
    public static String getEndSessionUrl(String baseUri, String userFlow) {
        return addSlash(baseUri) + String.format(END_SESSION_URL_WITHOUT_REDIRECT_URI_PATTERN, userFlow);
    }

    private static String addSlash(String uri) {
        return uri.endsWith("/") ? uri : uri + "/";
    }
}
