package com.zkos.helloworld.service;

import com.zkos.helloworld.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User addUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
}
