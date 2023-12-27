package com.ServiceMatch.SM.repository;

import com.ServiceMatch.SM.entities.AppUser;
import com.ServiceMatch.SM.enums.RolEnum;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
  @Query("SELECT a FROM AppUser a WHERE a.email = :email")
  public AppUser findByEmail(@Param("email") String email);

  @Query("SELECT a FROM AppUser a WHERE a.rol = :rol")
  public List<AppUser> findByRol(@Param("rol") RolEnum rol);
  
  
  @Query("SELECT DISTINCT au FROM AppUser au " +
           "INNER JOIN au.skills s " +
           "WHERE au.rol = 'PROVEEDOR' AND s.name = :skill")
    List<AppUser> findProvidersBySkill(@Param("skill") String skill);

}
