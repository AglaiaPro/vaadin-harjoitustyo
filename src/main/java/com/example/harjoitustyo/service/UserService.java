package com.example.harjoitustyo.service;

import com.example.harjoitustyo.model.AppUser;
import com.example.harjoitustyo.model.Role;
import com.example.harjoitustyo.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Transactional(readOnly = true)
    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional
    public AppUser register(String username, String email, String fullName, String rawPassword) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(Role.USER);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        AppUser saved = userRepository.save(user);
        mailService.notifyAdminNewUser(username, email);
        return saved;
    }

    @Transactional
    public AppUser save(AppUser user) {
        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(AppUser user, String rawPassword) {
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        userRepository.save(user);
    }

    @Transactional
    public void updatePhoto(AppUser user, String photoFileName) {
        user.setPhotoFileName(photoFileName);
        userRepository.save(user);
    }
}
