package com.swookiee.runtime.security.oauth2;

import java.io.InputStream;

public interface LoginTemplateProvider {

    InputStream getLoginTemplateInputStream();

}
