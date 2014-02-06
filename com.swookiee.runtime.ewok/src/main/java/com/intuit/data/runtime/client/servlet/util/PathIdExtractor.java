package com.intuit.data.runtime.client.servlet.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is a helper to extract ids from rest like paths. Example <code>/42</code> has the identifier
 * <code>42</code>. More complex <i>templates</i> could also get implemented in this class.
 * 
 */
public class PathIdExtractor {

    private final Pattern idPattern;

    public PathIdExtractor() {
        idPattern = Pattern.compile("/([0-9]+)");
    }

    /**
     * 
     * This method extracts ids as long from rest like paths.
     * 
     * @param path
     *            String of a servlet pathInfo
     * @return Identifier as long
     * @throws HttpErrorException
     */
    public long getId(final String path) throws HttpErrorException {
        final Matcher matcher = idPattern.matcher(path);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new HttpErrorException("Invalid URL", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}