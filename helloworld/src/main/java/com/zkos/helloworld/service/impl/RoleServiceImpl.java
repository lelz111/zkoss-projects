package com.zkos.helloworld.service.impl;

import com.zkos.helloworld.model.Role;
import com.zkos.helloworld.service.RoleService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Role findByName(String name) {
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}