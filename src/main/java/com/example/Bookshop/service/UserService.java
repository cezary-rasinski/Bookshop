package com.example.Bookshop.service;

import com.example.Bookshop.dto.UserRequest;
import com.example.Bookshop.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void register(UserRequest req);

    List<User> getUsers();
    Optional<User> findByLogin(String login);

    void deleteUser(String login);

}
