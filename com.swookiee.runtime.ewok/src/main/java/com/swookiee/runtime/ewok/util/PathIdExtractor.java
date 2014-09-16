/*******************************************************************************
 * Copyright (c) 2014 Lars Pfannenschmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Lars Pfannenschmidt - initial API and implementation, ongoing development and documentation
 *******************************************************************************/

package com.swookiee.runtime.ewok.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is a helper to extract IDs from REST like paths. Example <code>/42</code> has the identifier
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
     * This method extracts ids as long from REST like paths.
     * 
     * @param path
     *            String of a {@link Servlet} pathInfo
     * @return Identifier as long
     * @throws HttpErrorException in case the path could not be parsed
     */
    public long getId(final String path) throws HttpErrorException {
        final Matcher matcher = idPattern.matcher(path);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new HttpErrorException("Invalid URL", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

}