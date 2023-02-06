package com.example.azure.aad.b2c.security;

import com.azure.spring.cloud.autoconfigure.aadb2c.properties.AadB2cProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class CustomSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClientRegistrationRepository clientRegistrationRepository(AadB2cProperties properties,
                                                                     ObjectProvider<Tuple2<List<ClientRegistration>, Set<String>>> registrations) {
        final CustomAadB2cClientRegistrationsBuilder clientRegistrationsBuilder = new CustomAadB2cClientRegistrationsBuilder()
            .clientId(properties.getCredential().getClientId())
            .clientSecret(properties.getCredential().getClientSecret())
            .tenantId(properties.getProfile().getTenantId())
            .baseUri(properties.getBaseUri())
            .signInUserFlow(properties.getUserFlows().get(properties.getLoginFlow()))
            .replyUrl(properties.getReplyUrl())
            .userNameAttributeName(properties.getUserNameAttributeName())
            .userFlows(properties.getUserFlows()
                                 .entrySet()
                                 .stream()
                                 .filter(entry -> !entry.getKey().equals(properties.getLoginFlow()))
                                 .map(entry -> entry.getValue())
                                 .toArray(String[]::new));
        properties.getAuthorizationClients()
                  .entrySet()
                  .forEach(entry ->
                      clientRegistrationsBuilder.authorizationClient(
                          entry.getKey(),
                          entry.getValue().getAuthorizationGrantType(),
                          entry.getValue().getScopes().toArray(new String[0])
                      )
                  );
        final List<ClientRegistration> clientRegistrations = new ArrayList<>();
        final Set<String> nonSignInClientRegistrationIds = new HashSet<>();
        Tuple2<List<ClientRegistration>, Set<String>> defaultRegistrations = clientRegistrationsBuilder.build();
        clientRegistrations.addAll(defaultRegistrations.getT1());
        nonSignInClientRegistrationIds.addAll(defaultRegistrations.getT2());

        registrations.orderedStream().forEach(registration -> {
            clientRegistrations.addAll(registration.getT1());
            nonSignInClientRegistrationIds.addAll(registration.getT2());
        });
        return new CustomAadB2cClientRegistrationRepository(clientRegistrations, nonSignInClientRegistrationIds);
    }

    @Bean
    Tuple2<List<ClientRegistration>, Set<String>> aadB2cClientRegistrations1() {
        return new CustomAadB2cClientRegistrationsBuilder()
            .clientId("xxx-client-id-2-in-xxxb2c1")
            .clientSecret("xxx")
            .baseUri("https://xxxb2c1.b2clogin.com/xxxadb2c1.onmicrosoft.com")
            .signInUserFlow("B2C_1_signuporsignin2")
            .userFlows("B2C_1_passwordreset2")
            .build();
    }

    @Bean
    Tuple2<List<ClientRegistration>, Set<String>> aadB2cClientRegistrations2() {
        return new CustomAadB2cClientRegistrationsBuilder()
            .clientId("xxx-client-id-1-in-xxxb2c2")
            .clientSecret("WO38Q~P.WLOAP.pxCWe9Iqtv2qnDahvIBXthxcx4")
            .baseUri("https://xxxb2c2.b2clogin.com/xxxadb2c.onmicrosoft.com")
            .signInUserFlow("B2C_1_signuporsignin_fight")
            .userFlows("B2C_1_passwordreset_fight")
            .build();
    }
}
