
package com.ServiceMatch.SM.repository;

import com.ServiceMatch.SM.entities.Job;
import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.enums.RolEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    Provider findByName(String name);

    public List<Provider> findByRol(RolEnum rol);
    
     
    @Query("SELECT DISTINCT j FROM Job j INNER JOIN j.provider p WHERE p.rol = 'PROVEEDOR' AND p.id = :userId")
List<Job> findByProvider(@Param("userId") Long userId);

}
