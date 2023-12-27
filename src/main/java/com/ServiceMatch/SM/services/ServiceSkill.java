package com.ServiceMatch.SM.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ServiceMatch.SM.entities.Skill;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
public class ServiceSkill {

    @Autowired
    private SkillRepository skillRepository;

    @Transactional
    public void createSkill(String name) throws MyException {
        validate(name);
        Skill skill = new Skill();
        skill.setName(name);
        skill.setActive(true);
        skillRepository.save(skill);
    }

    public List<Skill> getSkills() {
        List<Skill> skills = new ArrayList<>();
        skills = skillRepository.findAll();
        return skills;
    }

    @Transactional
    public void deleteSkill(Long id) {
        Optional<Skill> result = skillRepository.findById(id);
        Skill skill = new Skill();
        if (result.isPresent()) {
            skill = result.get();
            skillRepository.delete(skill);
        }
    }

    @Transactional
    public void modifySkill(Long id, String name, boolean active) throws MyException {

        validate(name);
        Optional<Skill> result = skillRepository.findById(id);
        Skill skill = new Skill();
        if (result.isPresent()) {
            skill = result.get();
            skill.setName(name);
            skill.setActive(active);
            skillRepository.save(skill);
        }
    }

    @Transactional
    public void restoreSkill(Long id, boolean active) throws MyException {

        Optional<Skill> result = skillRepository.findById(id);
        Skill skill = new Skill();
        if (result.isPresent()) {
            skill = result.get();
            skill.setActive(active);
            skillRepository.save(skill);
        }
    }

    public Skill getOne(Long id){
        return skillRepository.findById(id).get();
    }

    public void validate(String name) throws MyException {

        if (name.isEmpty() || name == null) {

            throw new MyException("El nombre del oficio no puede ser nulo");
        }
    }

    public Page<Skill> getPageOfSkills(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return skillRepository.findAll(pageable);
    }

}
