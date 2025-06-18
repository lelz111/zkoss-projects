package com.zkos.helloworld.service;

import com.zkos.helloworld.model.User;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();

    public UserServiceImpl() {
        users.add(new User("001", "Rina", "Manager", "Aktif", null));
        users.add(new User("002", "Budi", "Staff", "Non-Aktif", null));
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void updateUser(int index, User user) {
        users.set(index, user);
    }

    @Override
    public void deleteUser(int index) {
        users.remove(index);
    }

    @Override
    public User getUser(int index) {
        return users.get(index);
    }
}

