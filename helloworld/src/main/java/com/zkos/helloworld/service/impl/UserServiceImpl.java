package com.zkos.helloworld.service.impl;

import com.zkos.helloworld.model.User;
import com.zkos.helloworld.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("userService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String npk) throws UsernameNotFoundException {
        User user = userDao.getByNpk(npk);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with NPK: " + npk);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.getNpk(),
                user.getPassword(),
                authorities
        );
    }
    @Override
    public void softDeleteUser(Long id) {
        User user = userDao.get(id);
        if (user != null) {
            user.setStatus("INACTIVE"); // or any status to mark as deleted
            userDao.update(user);
        }
    }

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