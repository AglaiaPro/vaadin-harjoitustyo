package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.AppUser;
import com.example.harjoitustyo.model.PasswordResetToken;
import com.example.harjoitustyo.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final String baseUrl;

    public PasswordResetService(UserService userService,
                                PasswordResetTokenRepository tokenRepository,
                                MailService mailService,
                                @Value("${app.base-url}") String baseUrl) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.mailService = mailService;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public Optional<String> createResetToken(String email) {
        Optional<AppUser> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user.get());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS));
        tokenRepository.save(token);

        String url = baseUrl + "/reset-password/" + token.getToken();
        mailService.sendPasswordReset(user.get().getEmail(), url);
        return Optional.of(url);
    }

    @Transactional
    public boolean resetPassword(String tokenValue, String rawPassword) {
        PasswordResetToken token = tokenRepository.findByToken(tokenValue).orElse(null);
        if (token == null || token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            return false;
        }
        userService.updatePassword(token.getUser(), rawPassword);
        token.setUsed(true);
        tokenRepository.save(token);
        return true;
    }
}
