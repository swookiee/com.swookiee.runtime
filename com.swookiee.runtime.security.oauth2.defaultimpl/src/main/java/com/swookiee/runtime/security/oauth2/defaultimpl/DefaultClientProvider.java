package com.swookiee.runtime.security.oauth2.defaultimpl;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;
import com.swookiee.runtime.security.oauth2.ClientProvider;
import com.swookiee.runtime.security.oauth2.OAuthClient;

@Component(service = ClientProvider.class)
public class DefaultClientProvider implements ClientProvider {

    @Override
    public List<OAuthClient> getClients() {
        return Lists.newArrayList(new OAuthClient("default", "default", Lists.newArrayList("http://localhost")));
    }

}
