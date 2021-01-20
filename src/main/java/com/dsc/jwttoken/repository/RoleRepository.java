package com.dsc.jwttoken.repository;

import com.dsc.jwttoken.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Short> {
    Role getByRoleName(String roleName);
}
