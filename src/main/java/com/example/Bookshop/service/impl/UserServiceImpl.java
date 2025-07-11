package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.UserRequest;
import com.example.Bookshop.model.User;
import com.example.Bookshop.repository.UserRepository;
import com.example.Bookshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(UserRequest req) {
        if (userRepository.findByLogin(req.getLogin()).isPresent()) {
            throw new IllegalArgumentException("User already registered!");
        }

        User u = User.builder()
                .id(UUID.randomUUID().toString())
                .login(req.getLogin())
                .password(passwordEncoder.encode(req.getPassword()))
                .role("USER")
                .isActive(true)
                .build();

        userRepository.save(u);
    }
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public void deleteUser(String login) {
        userRepository.findByLogin(login).ifPresent(u -> {
            u.setActive(false);
            userRepository.save(u);
        });
    }
}
