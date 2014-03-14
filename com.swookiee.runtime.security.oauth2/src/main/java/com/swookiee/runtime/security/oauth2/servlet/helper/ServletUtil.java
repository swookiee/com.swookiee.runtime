package com.swookiee.runtime.security.oauth2.servlet.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.swookiee.runtime.security.oauth2.servlet.AbstractOAuthServlet.ServletError;

public class ServletUtil {

    public static void writeHtmlToOutput(HttpServletResponse response, InputStream inputStream) throws IOException {
        writeHtmlToOutput(response, inputStream, (ServletError) null);
    }

    public static void writeHtmlToOutput(HttpServletResponse response, InputStream inputStream,
            Map<String, String> variables) throws IOException {
        String htmlFile = readHtmlFile(inputStream);
        Set<Entry<String, String>> entrySet = variables.entrySet();
        for (Entry<String, String> entry : entrySet) {
            htmlFile = htmlFile.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        response.getWriter().write(htmlFile);
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
    }

    public static void writeHtmlToOutput(HttpServletResponse response, InputStream inputStream,
            ServletError servletError) throws IOException {
        HashMap<String, String> variables = new HashMap<String, String>();
        if (servletError != null) {
            variables.put("SHOW_ERROR", "true");
            variables.put("ERROR", servletError.getName());
        } else {
            variables.put("SHOW_ERROR", "false");
            variables.put("ERROR", "");
        }
        writeHtmlToOutput(response, inputStream, variables);
    }

    private static String readHtmlFile(InputStream inputStream) throws IOException {
        String content = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        inputStream.close();
        return content;
    }
}
