package com.swookiee.runtime.security.oauth2;

import java.util.List;

public interface ClientProvider {

    List<OAuthClient> getClients();

}
