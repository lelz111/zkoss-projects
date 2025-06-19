package com.zkos.helloworld.service.impl;

import com.zkos.helloworld.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;

import java.util.List;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<User> queryAll() {
        Query query = em.createQuery("SELECT u FROM User u");
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public User get(Long id) {
        return em.find(User.class, id);
    }

    @Transactional
    public User save(User user) {
        em.persist(user);
        em.flush();
        return user;
    }

    @Transactional
    public User update(User user) {
        return em.merge(user);
    }

    @Transactional
    public void delete(Long id) {
        User existing = em.find(User.class, id);
        if (existing != null) {
            em.remove(existing);
        }
    }
}
