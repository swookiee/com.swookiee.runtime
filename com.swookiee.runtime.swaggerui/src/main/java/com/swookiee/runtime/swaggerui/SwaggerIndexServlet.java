package com.swookiee.runtime.swaggerui;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;

public class SwaggerIndexServlet extends HttpServlet {

    private static final long serialVersionUID = 1620188061961787429L;
    private final String pageContent;
    private final List<Bundle> apiBundles;

    public SwaggerIndexServlet(final String pageContent, final List<Bundle> apiBundles) {
        this.pageContent = pageContent;
        this.apiBundles = apiBundles;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final StringBuilder sb = new StringBuilder();
        for (final Bundle bundle : apiBundles) {
            sb.append(createButtonDiv(bundle.getSymbolicName()));
        }
        final String replaced = this.pageContent.replace("{BUNDLE-APIS}", sb.toString());

        response.getWriter().print(replaced);
    }

    private String createButtonDiv(final String bundleSymbolicName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<div class='input icon-btn'>");
        sb.append(String.format(
                "<img id='show-api-%1$2s' src='images/wordnik_api.png' title='%1$2s API' onclick=\"loadApi('/apidocs/%1$2s/')\">",
                bundleSymbolicName));
        sb.append("</div>");

        return sb.toString();
    }

}