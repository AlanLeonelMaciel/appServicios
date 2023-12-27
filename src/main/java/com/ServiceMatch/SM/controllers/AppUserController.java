package com.ServiceMatch.SM.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ServiceMatch.SM.entities.AppUser;
import com.ServiceMatch.SM.entities.Job;
import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.entities.Skill;
import com.ServiceMatch.SM.enums.RolEnum;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.services.ServiceJob;
import com.ServiceMatch.SM.services.ServiceProvider;
import com.ServiceMatch.SM.services.ServiceSkill;
import com.ServiceMatch.SM.services.UserService;

@Controller
@RequestMapping("/user")
public class AppUserController {

    @Autowired
    ServiceSkill serviceSkill;
    @Autowired
    ServiceProvider serviceProvider;

    @Autowired
    UserService serviceUser;

    @Autowired
    ServiceJob serviceJob;

    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/list") // http://localhost:8080/user/list/id
    public String listUsers(Model model, @RequestParam(defaultValue = "0") int page) {
        // Muestra 10 usuarios por página
        // Se guarda la lista de usuarios en "users" en formato de página (10 por
        // página)
        Page<AppUser> users = serviceUser.getPageOfUsers(page, 10);
        // Se inyectan al modelo todos los usuarios "userList"
        model.addAttribute("userList", users.getContent());
        // Se agrega información de paginación al modelo
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        // Devuelve el nombre de la plantilla HTML a renderizar
        return "user_list.html";
    }

    // MÉTODO PARA ADMIN
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/modify/{id}") // http://localhost:8080/user/modify/id
    public String modifyUser(@PathVariable Long id, ModelMap model) {
        // Lógica para modificar la skill en la base de datos
        model.put("user", serviceUser.getOne(id));

        return "user_modify.html";
    }

    // MÉTODO PARA ADMIN EDITAR USUARIOS
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, String name, boolean active, MultipartFile archivo, ModelMap model) {
        Optional<Provider> esProvider = serviceProvider.getProviderById(id);
        try {
            if (esProvider.isPresent()) {
                serviceProvider.modifyProvider(archivo, id, name);
            }
            serviceUser.updateUser(id, name, active);
            return "redirect:../list";

        } catch (MyException ex) {
            model.put("error", ex.getMessage());
            return "user_modify.html";
        }

    }

    // MÉTODO PARA ADMIN
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/restore/{id}") // http://localhost:8080/user/restore/id
    public String restoreSkill(@PathVariable Long id, boolean active) {
        try {
            active = true;
            serviceUser.restoreUser(id, active);

            return "redirect:../list";

        } catch (Exception e) {
            return "redirect:../list";

        }
    }

    // MÉTODO PARA ADMIN
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/delete/{id}") // http://localhost:8080/user/delete/id
    public String deleteSkill(@PathVariable Long id, ModelMap model) {
        // Lógica para eliminar la skill en la base
        serviceUser.deleteUser(id);
        return "redirect:../list";
    }

    // MÉTODO PARA EL REGISTRO DE UN USUARIO PROVEEDOR DEVUELVE VISTA CON SKILLS
    @GetMapping("/registration") // http://localhost:8080/user/registration
    public String showUserRegistrationForm(Model model) {
        List<Skill> skills = serviceSkill.getSkills();
        model.addAttribute("skillsRegistro", skills);
        return "register.html";
    }

    // MÉTODO PARA GUARDAR EL NUEVO USUARIO SEGÚN ROL
    @PostMapping("/save")
    public String saveUser(
            @RequestParam(required = false) MultipartFile archivo,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam(required = false) Long whatsApp,
            @RequestParam(required = false) List<Skill> skills,
            @RequestParam String role,
            Model model) {
        try {
            if (role.equals("client")) {
                serviceUser.registrar(name, email, password, password2);
                return "redirect:/";
            }
            serviceProvider.registrar(archivo, name, email, password, password2, whatsApp, skills);
            model.addAttribute("message", "User '" + name + "' saved successfully");
        } catch (MyException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register.html";
        }
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @GetMapping("/providers")
    public String providerList(@RequestParam(name = "skill", required = false) String skill,
            @RequestParam(name = "id", required = false) Long id, ModelMap model) {
        List<Skill> skills = serviceSkill.getSkills();
        model.addAttribute("skills", skills);
        if (skill != null) {
            List<AppUser> providers = serviceUser.loadUserBySkyll(skill);
            model.addAttribute("providers", providers);
            model.addAttribute("selectedSkill", skill);
        } else {
            List<AppUser> providers = serviceUser.loadUserBySkyll("Plomero");
            model.addAttribute("providers", providers);
            model.addAttribute("selectedSkill", "Plomero");
        }
        List<Job> jobs = serviceJob.listJobByProvider(id);
        double totalCalification = 0.0;

        for (Job job : jobs) {
            Long callification = job.getCallification();
            if (callification != null) {
                totalCalification += callification;
            }
        }
        double averageCalification = jobs.isEmpty() ? 0.0 : totalCalification / jobs.size();
        BigDecimal roundedAverage = new BigDecimal(averageCalification).setScale(1, RoundingMode.HALF_UP);
        model.addAttribute("averageCalification", roundedAverage);

        return "provider_list.html";
    }

    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @GetMapping("/providers/{skill}")
    public String userProviderList(@RequestParam String skill, ModelMap model, RedirectAttributes redirectAttributes) {
        List<AppUser> providers = serviceUser.loadUserBySkyll(skill);
        model.addAttribute("providers", providers);

        model.addAttribute("selectedSkill", skill);

        redirectAttributes.addAttribute("skill", skill);

        return "redirect:/user/providers";

    }

    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @GetMapping("providers/details/{id}")
    public String providerDetails(@PathVariable Long id, ModelMap model) {
        try {
            List<Job> jobs = serviceJob.listJobByProvider(id);
            AppUser provider = serviceUser.getOne(id);

            double totalCalification = 0.0;

            for (Job job : jobs) {
                Long callification = job.getCallification();
                if (callification != null) {
                    totalCalification += callification;
                }
            }
            double averageCalification = jobs.isEmpty() ? 0.0 : totalCalification / jobs.size();
            BigDecimal roundedAverage = new BigDecimal(averageCalification).setScale(1, RoundingMode.HALF_UP);
            model.addAttribute("provider", provider);
            model.addAttribute("providerName", jobs.isEmpty() ? "" : jobs.get(0).getProvider().getName());
            model.addAttribute("comments", jobs.stream().map(Job::getComment).collect(Collectors.toList()));
            model.addAttribute("averageCalification", roundedAverage);
            model.addAttribute("jobs", jobs);

            return "provider_details";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "Proveedor no encontrado");
            return "redirect:/user/providers";
        }
    }

    @PreAuthorize("hasRole('ROLE_PROVEEDOR')")
    @GetMapping("providers/calification/{id}")
    public String providerCali(@PathVariable Long id, ModelMap model) {
        try {
            List<Job> jobs = serviceJob.listJobByProvider(id);
            AppUser provider = serviceUser.getOne(id);

            double totalCalification = 0.0;

            for (Job job : jobs) {
                Long callification = job.getCallification();
                if (callification != null) {
                    totalCalification += callification;
                }
            }
            double averageCalification = jobs.isEmpty() ? 0.0 : totalCalification / jobs.size();
            BigDecimal roundedAverage = new BigDecimal(averageCalification).setScale(1, RoundingMode.HALF_UP);
            model.addAttribute("provider", provider);
            model.addAttribute("providerName", jobs.isEmpty() ? "" : jobs.get(0).getProvider().getName());
            model.addAttribute("comments", jobs.stream().map(Job::getComment).collect(Collectors.toList()));
            model.addAttribute("averageCalification", roundedAverage);
            model.addAttribute("jobs", jobs);

            return "provider_calification.html";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", "Proveedor no encontrado");
            return "redirect:/";
        }
    }

    // MÉTODO PARA DEVOLVER VISTA EDITAR PERFIL TANTO PARA CLIENTE COMO PARA
    // PROVEEDOR
    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR', 'ROLE_USUARIO')")
    @GetMapping("/editprofile/{id}")
    public String userProfile(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            AppUser user = serviceUser.getOne(id);
            List<Skill> skills = serviceSkill.getSkills();
            String error = (String) redirectAttributes.getFlashAttributes().get("error");
            if (error != null) {
                model.addAttribute("error", error);
            }

            model.addAttribute("skillsRegistro", skills);
            model.addAttribute("user", user);
            if (user.getRol().equals(RolEnum.USUARIO)) {
                model.addAttribute("client", user);
                return "client_profile.html";
            }
            if (user.getRol().equals(RolEnum.PROVEEDOR)) {
                Provider provider = serviceProvider.getOne(id);
                model.addAttribute("provider", provider);
                return "provider_profile.html";
            } else {
                return "redirect:/";
            }

        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "redirect:/";

        }
    }

    // MÉTODO PARA EDITAR PERFIL CLIENTE
    @PostMapping("/editprofile/{id}")
    public String clientProfile(@PathVariable Long id, @RequestParam String name, @RequestParam String password,
            @RequestParam String password2, @RequestParam(required = false) Long whatsApp,
            @RequestParam(required = false) List<Skill> skills, @RequestParam String role,
            @RequestParam(required = false) MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (role.equals("client")) {
                serviceUser.editClient(id, name, password, password2);
                model.addAttribute("exito", "Cambios guardados en perfil");
                return "redirect:/";
            }
            if (role.equals("provider")) {
                serviceUser.clientToProvider(id, name, password, password2, whatsApp, skills, file);
                return "redirect:/";
            } else {
                return "/user/client/editprofile/";
            }
        } catch (MyException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/user/editprofile/" + id;
        }

    }

    // método para editar perfil proveedor
    @PostMapping("/provider/editprofile/{id}")
    public String providerProfile(@PathVariable Long id, @RequestParam String name, @RequestParam String password,
            @RequestParam String password2, @RequestParam(required = false) Long whatsApp,
            @RequestParam(required = false) List<Skill> skills, @RequestParam(required = false) String role,
            @RequestParam(required = false) MultipartFile file, Model model) {
        try {
            serviceUser.editProvider(id, name, password, password2, whatsApp, skills, file);
            model.addAttribute("exito", "Cambios guardados con éxito.");
            return "redirect:/";
        } catch (MyException ex) {
            model.addAttribute("error", ex.getMessage());
            return "redirect:/";
        }
    }

}
