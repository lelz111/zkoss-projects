package com.zkos.helloworld.service;

import com.zkos.helloworld.model.Role;

public interface RoleService {
    Role findByName(String name);
}