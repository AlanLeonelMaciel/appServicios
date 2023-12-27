package com.ServiceMatch.SM.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ServiceMatch.SM.entities.AppUser;
import com.ServiceMatch.SM.entities.Job;
import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.entities.Skill;
import com.ServiceMatch.SM.enums.JobStatusEnum;
import com.ServiceMatch.SM.enums.RolEnum;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.services.ServiceJob;
import com.ServiceMatch.SM.services.ServiceProvider;
import com.ServiceMatch.SM.services.ServiceSkill;
import com.ServiceMatch.SM.services.UserService;

@Controller
@RequestMapping("/job")
public class JobControler {

    @Autowired
    ServiceJob serviceJob;
    @Autowired
    ServiceSkill serviceSkill;
    @Autowired
    UserService userService;
    @Autowired
    ServiceProvider serviceProvider;

    // REQUEST CREAR JOB (LO SOLICITA EL CLIENTE) TRAYENDO ID DE PROVIDER
    // SELECCIONADO
    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @GetMapping("/create/{idProvider}") // http://localhost:8080/job/create
    public String createJob(HttpSession session, @PathVariable Long idProvider,
            Model model) {
        Provider provider = serviceProvider.getOne(idProvider);
        AppUser client = (AppUser) session.getAttribute("usuariosession");
        Long idClient = client.getId();
        System.out.println("ID CLIENTE " + idClient);
        List<Skill> skills = provider.getSkills();
        model.addAttribute("skills", skills);
        model.addAttribute("client", client);
        model.addAttribute("provider", provider);
        return "create_job.html";
    }

    @PostMapping("/create/{idProvider}")
    public String createJob(@RequestParam(required = false) String description,
            Long idSkill, Long idClient,
            @RequestParam(required = false) Long idProvider,
            RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) {
        try {
            serviceJob.createJob(description, idSkill, idClient, idProvider);
            redirectAttributes.addFlashAttribute("exito", "La solicitud fue enviada correctamente");
        } catch (MyException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        String currentPath = request.getRequestURI();
        return "redirect:" + currentPath;
    }

    // SERGIO METODO MODIFICADO Y FUNCIONANDO
    // LISTAR JOBS DE UN USUARIO LOGUEADO CLIENT O PROVIDER
    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR', 'ROLE_USUARIO')")
    @GetMapping("/list/provider/{id}")
    public String jobListPrueba(@PathVariable Long id, HttpSession session, Model model) {
        AppUser client = (AppUser) session.getAttribute("usuariosession");
        model.addAttribute(session);
        if (client.getRol() == RolEnum.USUARIO) {
            List<Job> jobs = serviceJob.listByIdClient(client.getId());
            model.addAttribute("jobs", jobs);
        } else {
            List<Job> jobs = serviceJob.listByIdProvider(id);
            model.addAttribute("jobs", jobs);
        }
        return "job_list.html";

    }

    // MÉTODO GET Y POST PARA MODIFICAR JOB
    @GetMapping("/modify/{id}") // http://localhost:8080/job/modify/id
    public String modifyJob(@PathVariable Long id, ModelMap model) {
        model.put("job", serviceJob.getOne(id));
        return "job_modify.html";
    }

    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, String description, Double cost, ModelMap model) {
        try {
            serviceJob.modifyJob(id, description, cost);
            model.put("exito", "El job fue modificado correctamente!");
            return "redirect:/job/modify/" + id;
        } catch (MyException ex) {
            model.put("error", ex.getMessage());
            return "redirect:/job/modify/" + id;
        }
    }

    // PRESUPUESTAR JOB SOLICITADO POR CLIENT (PROVIDER)
    @PreAuthorize("hasRole('ROLE_PROVEEDOR')")
    @PostMapping("/list/provider/{id}/budget/{idJob}")
    public String budgetJob(@PathVariable Long id, @PathVariable Long idJob, String status, @RequestParam Double cost,
            ModelMap model) {
        try {
            serviceJob.updateJobStatus(idJob, JobStatusEnum.BUDGETED);
            serviceJob.updateCost(idJob, cost);

            model.put("exito", "Job actualizado correctamente");
            return "redirect:/job/list/provider/{id}";
        } catch (MyException ex) {
            model.put("error", "Error al actualizar el job: " + ex.getMessage());
            return "forward:/job/list/provider/{id}"; // Reenvía a la misma vista con mensajes de error
        }
    }

    // ACEPTAR JOB PRESUPUESTADO (CLIENT)
    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @PostMapping("/list/provider/{id}/accept/{idJob}")
    public String acceptJob(@PathVariable Long id, @PathVariable Long idJob, String status, ModelMap model) {
        try {
            serviceJob.updateJobStatus(idJob, JobStatusEnum.ACCEPTED);

            model.put("exito", "Job actualizado correctamente");
            return "redirect:/job/list/provider/{id}";
        } catch (MyException ex) {
            model.put("error", "Error al actualizar el job: " + ex.getMessage());
            return "forward:/job/list/provider/{id}"; // Reenvía a la misma vista con mensajes de error
        }
    }

    // RECHAZAR JOB (CLIENT Y PROVIDER)
    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR', 'ROLE_USUARIO')")
    @PostMapping("/list/provider/{id}/refused/{idJob}")
    public String refusedJob(@PathVariable Long id, @PathVariable Long idJob, String status, ModelMap model) {
        try {
            serviceJob.updateJobStatus(idJob, JobStatusEnum.REFUSED);
            model.put("exito", "Job actualizado correctamente");
            return "redirect:/job/list/provider/{id}";
        } catch (MyException ex) {
            model.put("error", "Error al actualizar el job: " + ex.getMessage());
            return "forward:/job/list/provider/{id}"; // Reenvía a la misma vista con mensajes de error
        }
    }

    // FINALIZAR JOB (CLIENT Y PROVIDER)
    @PreAuthorize("hasAnyRole('ROLE_PROVEEDOR', 'ROLE_USUARIO')")
    @PostMapping("/list/provider/{id}/end/{idJob}")
    public String endJob(@PathVariable Long id, @PathVariable Long idJob, String status, ModelMap model) {
        try {
            serviceJob.updateJobStatus(idJob, JobStatusEnum.END);
            model.put("exito", "Job actualizado correctamente");
            return "redirect:/job/list/provider/{id}";
        } catch (MyException ex) {
            model.put("error", "Error al actualizar el job: " + ex.getMessage());
            return "forward:/job/list/provider/{id}";
        }
    }

    // MÉTODO PARA LISTAR COMENTARIOS DE JOBS
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/comment/list") // http://localhost:8080/user/list/id
    public String listComments(Model model) {
        List<Job> jobs = serviceJob.listJobs();
        model.addAttribute("jobs", jobs);
        return "comment_list.html";
    }

    // MÉTODO PARA CENSURAR COMENTARIOS
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    @GetMapping("/comment/censor/{id}")
    public String eliminarResenia(@PathVariable Long id, ModelMap model) {
        try {
            serviceJob.censorComment(id);
            model.put("exito", "Comentario censurado con exito");
            return "redirect:/job/comment/list";
        } catch (Exception e) {
            model.put("error", e.getMessage());
            return "redirect:/job/comment/list";
        }
    }

    // COMENTARIOS Y CALIFICACION
    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @GetMapping("/rating/{id}")
    public String formComment(@PathVariable Long id, ModelMap model) {
        Job job = serviceJob.getOne(id);
        model.addAttribute("job", job);
        return "rating_comments.html";
    }

    @PreAuthorize("hasRole('ROLE_USUARIO')")
    @PostMapping("rating/{id}") // id del Job
    public String ratingAndComment(@PathVariable Long id, @RequestParam(required = false) Long callification,
            @RequestParam String comment, ModelMap model) {
        System.out.println("Calification: " + callification);
        try {
            serviceJob.createComment(id, callification, comment.toUpperCase());
            model.put("exito", "Job actualizado correctamente");
        } catch (MyException ex) {
            model.put("error", "Error al cargar la calificación y los comentarios. " + ex.getMessage());
            return "forward:/job/user/rating/{id}"; // Reenvía a la misma vista con mensajes de error
        }
        return "redirect:/job/rating/{id}";
    }

}
