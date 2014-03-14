package com.swookiee.runtime.security.oauth2.client;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;
import com.swookiee.runtime.security.oauth2.ClientProvider;
import com.swookiee.runtime.security.oauth2.OAuthClient;

@Component(service = ClientRegistry.class)
public class ClientRegistry {

    private final Map<String, OAuthClient> clients = Maps.newConcurrentMap();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addClientProvider(ClientProvider clientProvider) {
        for (OAuthClient client : clientProvider.getClients()) {
            add(client);
        }
    }

    public boolean clientExists(String clientId) {
        return clientId != null ? clients.containsKey(clientId) : false;
    }

    public boolean isValidClientIdAndClientSecret(String clientId, String clientSecret) {

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

        return true;
    }

    public boolean isValidClientIdAndRedirectUri(String clientId, String redirectUri) {

        OAuthClient oAuthClient = clients.get(clientId);

        if (oAuthClient == null) {
            return false;
        }

        if (!oAuthClient.getRedirectUris().contains(redirectUri)) {
            return false;
        }

        return true;
    }

    public void removeClientProvider(ClientProvider clientProvider) {
        for (OAuthClient client : clientProvider.getClients()) {
            remove(client);
        }
    }

    private void add(OAuthClient client) {
        clients.put(client.getId(), client);
    }

    private void remove(OAuthClient client) {
        clients.remove(client);
    }

}
