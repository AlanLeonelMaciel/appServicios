package com.ServiceMatch.SM.controllers;

import com.ServiceMatch.SM.entities.Skill;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.services.ServiceSkill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/skill")
public class SkillController {

    @Autowired
    ServiceSkill serviceSkill;

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/list") // http://localhost:8080/skill/list
    public String listSkills(@RequestParam(required = false) String success, Model model,
            @RequestParam(defaultValue = "0") int page) {
        // Muestra 15 skills
        Page<Skill> skills = serviceSkill.getPageOfSkills(page, 15);

        model.addAttribute("skillsList", skills.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", skills.getTotalPages());
        if (success != null) {
            model.addAttribute("exito", success);
        }
        return "skill_list.html";
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/registration") // http://localhost:8080/skill/registration
    public String showSkillRegistrationForm() {
        return "skill_registration.html";
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @PostMapping("/save") // http://localhost:8080/skill/save
    public String saveSkill(@RequestParam String skillName, Model model) {
        // Lógica para guardar la skill en la base de datos
        try {
            serviceSkill.createSkill(skillName);
        } catch (MyException ex) {
            model.addAttribute("error", ex.getMessage());
            return "skill_registration.html";
        }
        return "redirect:../skill/list?success=Skill '" + skillName + "' saved successfully";
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/modify/{id}") // http://localhost:8080/skill/modify/id
    public String modifySkill(@PathVariable Long id, ModelMap model) {
        // Lógica para modificar la skill en la base de datos
        model.put("skill", serviceSkill.getOne(id));

        return "skill_modify.html";
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, String name, boolean active, ModelMap model) {

        try {
            serviceSkill.modifySkill(id, name, active);
            return "redirect:../list";

        } catch (MyException ex) {
            model.put("error", ex.getMessage());
            return "skill_modify.html";
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/restore/{id}")
    public String restoreSkill(@PathVariable Long id, boolean active) {
        try {
            active = true;
            serviceSkill.restoreSkill(id, active);
            return "redirect:../list";

        } catch (Exception e) {
            return "redirect:../list";

        }
    }

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/delete/{id}") // http://localhost:8080/skill/delete/id
    public String deleteSkill(@PathVariable Long id, ModelMap model) {
        // Lógica para eliminar la skill en la base
        serviceSkill.deleteSkill(id);
        return "redirect:../list";
    }

}
