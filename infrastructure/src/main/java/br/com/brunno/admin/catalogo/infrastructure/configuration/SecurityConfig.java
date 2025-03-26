package br.com.brunno.admin.catalogo.infrastructure.configuration;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    public static final String ROLE_ADMIN = "CATALOGO_ADMIN";
    public static final String ROLE_CAST_MEMBERS = "CATALOGO_CAST_MEMBERS";
    public static final String ROLE_GENRES = "CATALOGO_GENRES";
    public static final String ROLE_CATEGORIES = "CATALOGO_CATEGORIES";
    public static final String ROLE_VIDEOS = "CATALOGO_VIDEOS";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .antMatchers("/cast_members*").hasAnyRole(ROLE_ADMIN, ROLE_CAST_MEMBERS)
                                .antMatchers("/genres*").hasAnyRole(ROLE_ADMIN, ROLE_GENRES)
                                .antMatchers("/categories*").hasAnyRole(ROLE_ADMIN, ROLE_CATEGORIES)
                                .antMatchers("/videos*").hasAnyRole(ROLE_ADMIN, ROLE_VIDEOS)
                                .anyRequest().hasRole(ROLE_ADMIN)
                )
                .oauth2ResourceServer(oauth -> {
                    oauth.jwt().jwtAuthenticationConverter(new KeycloakConverter());
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers ->
                        headers.frameOptions().sameOrigin()
                )
                .build();
    }

    static class KeycloakConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        private final KeycloakAuthoritiesConverter keycloakAuthoritiesConverter;

        KeycloakConverter() {
            this.keycloakAuthoritiesConverter = new KeycloakAuthoritiesConverter();
        }

        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {
            return new JwtAuthenticationToken(jwt, extractAuthorities(jwt), extractPrincipal(jwt));
        }

        private String extractPrincipal(Jwt jwt) {
            return jwt.getClaimAsString(JwtClaimNames.SUB);
        }

        private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
            return this.keycloakAuthoritiesConverter.convert(jwt);
        }
    }

    static class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        public static final String ROLES = "roles";
        public static final String ROLE = "role";
        public static final String ROLE_SEPARATOR = "_";
        public static final String RESOURCE_ACCESS = "resource_access";
        public static final String REALM_ACCESS = "realm_access";
        public static final String ROLE_PREFIX = "ROLE_";

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            final var realmRoles = extractRealmRoles(jwt);
            final var resourceRoles = extractResourceRoles(jwt);

            return Stream.concat(realmRoles, resourceRoles)
                    .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()))
                    .collect(Collectors.toSet());
        }

        private Stream<String> extractResourceRoles(Jwt jwt) {
            final Function<Map.Entry<String,Object>, Stream<String>> mapResource = resource -> {
                final var key = resource.getKey();
                final var value = (JSONObject) resource.getValue();
                final var roles = (Collection<String>) value.get(ROLES);
                return roles.stream()
                        .map(role -> key.concat(ROLE_SEPARATOR).concat(ROLE));
            };

            final Function<Set<Map.Entry<String,Object>>, Collection<String>> mapResources = resources ->
                    resources.stream()
                            .flatMap(mapResource)
                            .toList();

            return Optional.ofNullable(jwt.getClaimAsMap(RESOURCE_ACCESS))
                    .map(Map::entrySet)
                    .map(mapResources)
                    .orElse(Collections.emptyList())
                    .stream();
        }

        private Stream<String> extractRealmRoles(Jwt jwt) {
            return Optional.ofNullable(jwt.getClaimAsMap(REALM_ACCESS))
                    .map(resource -> (Collection<String>) resource.get(ROLES))
                    .orElse(Collections.emptyList())
                    .stream();
        }
    }
}
