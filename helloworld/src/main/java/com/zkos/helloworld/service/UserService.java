package com.zkos.helloworld.service;

import com.zkos.helloworld.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    void addUser(User user);
    void updateUser(int index, User user);
    void deleteUser(int index);
    User getUser(int index);
}
