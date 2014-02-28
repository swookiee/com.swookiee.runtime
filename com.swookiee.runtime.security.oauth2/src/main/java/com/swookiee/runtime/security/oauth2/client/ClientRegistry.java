package com.swookiee.runtime.security.oauth2.client;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component(service = ClientRegistry.class)
public class ClientRegistry {

    private final Map<String, OAuthClient> clients = Maps.newConcurrentMap();

    public ClientRegistry() {
        List<OAuthClient> clients = Lists.newArrayList();
        clients.add(new OAuthClient("client1", Lists.newArrayList("http://google.de/")));
        for (OAuthClient oAuthClient : clients) {
            add(oAuthClient);
        }
    }

    public void add(OAuthClient client) {
        clients.put(client.getId(), client);
    }

    public boolean clientExists(String clientId) {
        return clientId != null ? clients.containsKey(clientId) : false;
    }

    public boolean isValid(String clientId, String redirectUri) {

        OAuthClient oAuthClient = clients.get(clientId);

        if (oAuthClient == null) {
            return false;
        }

        if (!oAuthClient.getRedirectUris().contains(redirectUri)) {
            return false;
        }

        return true;
    }

    public boolean isValid(String clientId, String clientSecret, String redirectUri) {

        OAuthClient oAuthClient = clients.get(clientId);

        if (oAuthClient == null) {
            return false;
        }

        String secret = oAuthClient.getSecret();
        if (secret != null) {
            if (!secret.equals(clientSecret)) {
                return false;
            }
        }

        if (!oAuthClient.getRedirectUris().contains(redirectUri)) {
            return false;
        }

        return true;
    }

    public void remove(OAuthClient client) {
        clients.remove(client);
    }


}
