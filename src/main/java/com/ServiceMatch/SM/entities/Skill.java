package com.ServiceMatch.SM.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Skill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    private String name;
    private boolean active;

    @ManyToMany(mappedBy = "skills")
    private List<Provider> providers;

    @Override
    public String toString() {
        return "Skill{id=" + id + ", skillName='" + name + ", active='" + active + "'}";
    }
}
