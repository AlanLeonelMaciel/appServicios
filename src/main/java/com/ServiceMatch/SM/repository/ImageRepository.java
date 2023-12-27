package com.ServiceMatch.SM.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ServiceMatch.SM.entities.Image;


@Repository
public interface ImageRepository  extends JpaRepository<Image,Long>{
}
