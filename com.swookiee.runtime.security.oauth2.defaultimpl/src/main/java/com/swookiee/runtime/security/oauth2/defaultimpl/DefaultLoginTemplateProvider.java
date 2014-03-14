package com.swookiee.runtime.security.oauth2.defaultimpl;

import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

import com.swookiee.runtime.security.oauth2.LoginTemplateProvider;

@Component(service = LoginTemplateProvider.class)
public class DefaultLoginTemplateProvider implements LoginTemplateProvider {

    @Override
    public InputStream getLoginTemplateInputStream() {
        return DefaultLoginTemplateProvider.class.getResourceAsStream("/res/login.html");
    }
}
