
package com.ServiceMatch.SM.repository;

import com.ServiceMatch.SM.entities.Job;
import com.ServiceMatch.SM.enums.RolEnum;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface JobRepository extends JpaRepository<Job, Long>{
    
   @Query("SELECT j FROM Job j WHERE j.callification = :callification")
public List<Job> findByCallification(@Param("callification") Long callification);


@Query("SELECT j FROM Job j WHERE j.startDate BETWEEN :startDate1 AND :startDate2 AND j.endDate BETWEEN :endDate1 AND :endDate2")
public List<Job> findByStartDateBetweenAndEndDateBetween(@Param("startDate1") Date startDate1, @Param("startDate2") Date startDate2, @Param("endDate1") Date endDate1, @Param("endDate2") Date endDate2);


@Query("SELECT j FROM Job j WHERE j.cost > :cost")
public List<Job> findByCostGreaterThan(@Param("cost") Double cost);

@Query("SELECT j FROM Job j WHERE j.jobStatus = :jobStatus")
public List<Job> findByJobStatus(@Param("jobStatus") RolEnum jobStatus);
    


@Query("SELECT j FROM Job j WHERE j.skill = :skillName")
public List<Job> findByJobSkill (@Param("skillName") String skillName );


@Query("SELECT j FROM Job j WHERE j.provider.id = :id")
public List<Job> findByAppUser(@Param("id") Long id);


@Query("SELECT j FROM Job j WHERE j.appUser.id = :id")
public List<Job> findByClient(@Param("id") Long id);
}
