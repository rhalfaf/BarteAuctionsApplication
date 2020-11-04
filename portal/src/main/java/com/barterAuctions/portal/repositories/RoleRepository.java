package com.barterAuctions.portal.repositories;

import com.barterAuctions.portal.models.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByRole(String role);





}
