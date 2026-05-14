package com.example.harjoitustyo.security;

import com.example.harjoitustyo.model.AppUser;
import com.example.harjoitustyo.model.Role;
import com.example.harjoitustyo.repository.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuth2AccountService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public OAuth2AccountService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String subject = oauthUser.getName();
        String email = readEmail(oauthUser, provider, subject);
        String displayName = readDisplayName(oauthUser, email);

        AppUser localUser = userRepository.findByOauthProviderAndOauthSubject(provider, subject)
                .or(() -> userRepository.findByEmailIgnoreCase(email))
                .orElseGet(AppUser::new);

        localUser.setUsername(localUser.getUsername() == null ? email : localUser.getUsername());
        localUser.setEmail(email);
        localUser.setFullName(displayName);
        localUser.setRole(localUser.getRole() == null ? Role.USER : localUser.getRole());
        localUser.setOauthProvider(provider);
        localUser.setOauthSubject(subject);
        if (localUser.getPasswordHash() == null) {
            localUser.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        }
        userRepository.save(localUser);

        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        attributes.put("local_username", localUser.getUsername());
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_" + localUser.getRole().name())),
                attributes,
                "local_username"
        );
    }

    private String readEmail(OAuth2User oauthUser, String provider, String subject) {
        Object email = oauthUser.getAttributes().get("email");
        if (email != null && !email.toString().isBlank()) {
            return email.toString();
        }
        Object login = oauthUser.getAttributes().get("login");
        if (login != null) {
            return login + "@" + provider + ".oauth.local";
        }
        return subject + "@" + provider + ".oauth.local";
    }

    private String readDisplayName(OAuth2User oauthUser, String email) {
        Object name = oauthUser.getAttributes().get("name");
        if (name != null && !name.toString().isBlank()) {
            return name.toString();
        }
        Object login = oauthUser.getAttributes().get("login");
        if (login != null && !login.toString().isBlank()) {
            return login.toString();
        }
        return email;
    }

}
