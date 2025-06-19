package com.zkos.helloworld.service.impl;

import com.zkos.helloworld.service.impl.UserDao;
import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> getAllUsers() {
        return userDao.queryAll();
    }

    @Override
    public User getUserById(Long id) {
        return userDao.get(id);
    }

    @Override
    public User addUser(User user) {
        return userDao.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userDao.update(user);
    }

    @Override
    public void deleteUser(Long id) {
        userDao.delete(id);
    }
}
