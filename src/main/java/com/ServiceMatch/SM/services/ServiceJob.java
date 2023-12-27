package com.ServiceMatch.SM.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ServiceMatch.SM.entities.AppUser;
import com.ServiceMatch.SM.entities.Job;
import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.entities.Skill;
import com.ServiceMatch.SM.enums.JobStatusEnum;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.repository.JobRepository;
import com.ServiceMatch.SM.repository.ProviderRepository;
import com.ServiceMatch.SM.repository.SkillRepository;
import com.ServiceMatch.SM.repository.UserRepository;

@Service
public class ServiceJob {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProviderRepository providerRepository;

    public void createJob(String description, Long idSkill, Long idUser, Long idProvider)
            throws MyException {

        validate(description, idSkill, idUser, idProvider);

        Optional<Skill> responseSkill = skillRepository.findById(idSkill);
        Optional<AppUser> responseUser = userRepository.findById(idUser);
        Optional<Provider> responseProvider = providerRepository.findById(idProvider);

        Skill skill = new Skill();
        AppUser appUser = new AppUser();
        Provider provider = new Provider();

        if (responseSkill.isPresent()) {

            skill = responseSkill.get();
        }

        if (responseUser.isPresent()) {

            appUser = responseUser.get();
        }

        if (responseProvider.isPresent()) {

            provider = responseProvider.get();

        }

        Job job = new Job();

        job.setDescription(description);
        job.setJobStatus(JobStatusEnum.PENDING);
        job.setProvider(provider);
        job.setAppUser(appUser);
        job.setSkill(skill);

        jobRepository.save(job);
    }

    public List<Job> listJobs() {

        List<Job> jobs = new ArrayList<>();

        jobs = jobRepository.findAll();

        return jobs;

    }

    public List<Job> listByIdProvider(Long id) {

        List<Job> jobs = new ArrayList<>();

        jobs = jobRepository.findByAppUser(id);
        return jobs;
    }

    // METODO NUEVO 12/12

    public List<Job> listByIdClient(Long id) {

        List<Job> jobs = new ArrayList<>();

        jobs = jobRepository.findByClient(id);
        return jobs;
    }

    @Transactional

    public void modifyJob(Long id, String comment, Double cost, Long callification, String description, Long idSkill,
            Long idUser, Long idProvider) throws MyException {

        validate(comment, cost, description, idSkill, idUser, idProvider);

        Optional<Job> responseJob = jobRepository.findById(id);

        Optional<Skill> responseSkill = skillRepository.findById(idSkill);
        Optional<AppUser> responseUser = userRepository.findById(idUser);
        Optional<Provider> responseProvider = providerRepository.findById(idProvider);

        Skill skill = new Skill();
        AppUser appUser = new AppUser();
        Provider provider = new Provider();
        Job job = new Job();

        if (responseJob.isPresent()) {

            job = responseJob.get();
        }

        if (responseSkill.isPresent()) {

            skill = responseSkill.get();
        }

        if (responseUser.isPresent()) {

            appUser = responseUser.get();
        }

        if (responseProvider.isPresent()) {

            provider = responseProvider.get();

        }

        job.setAppUser(appUser);
        job.setCallification(callification);
        job.setComment(comment);
        job.setCost(cost);
        job.setDescription(description);
        job.setProvider(provider);
        job.setSkill(skill);

        jobRepository.save(job);

    }

    // AQUÍ EMPIEZA PAULINA MÉTODO TEMPORAL PARA PODER PROBAR EL FRONT JOB_MODIFY
    @Transactional
    public void modifyJob(Long id, String description, Double cost) throws MyException {
        validateDescription(description, cost);
        Optional<Job> responseJob = jobRepository.findById(id);
        if (responseJob.isPresent()) {
            Job job = responseJob.get();
            job.setDescription(description);
            job.setCost(cost);
            jobRepository.save(job);
        } else {
            throw new MyException("No se encontró el trabajo con ID: " + id);
        }
    }

    // MÉTODO GET ONE
    public Job getOne(Long id) {
        return jobRepository.findById(id).get();
    }
    // AQUÍ TERMINA PAULINA

    public void updateJobStatus(Long id, JobStatusEnum status) throws MyException {
        Optional<Job> responseJob = jobRepository.findById(id);

        if (responseJob.isPresent()) {
            Job job = responseJob.get();

            job.setJobStatus(status);

            // Verificar si el estado cambia a "end" o "refused"
            if (JobStatusEnum.END.equals(status) || JobStatusEnum.REFUSED.equals(status)) {
                job.setEndDate(new Date());
            }
            // Guardar los cambios en el repositorio
            jobRepository.save(job);
        } else {
            throw new MyException("No se encontró el trabajo con el ID proporcionado: " + id);
        }
    }

    // SERGIO CARGAR COSTO AL JOB
    public void updateCost(Long id, Double cost) throws MyException {

        Optional<Job> responseJob = jobRepository.findById(id);

        if (responseJob.isPresent()) {
            Job job = responseJob.get();
            job.setCost(cost);
            Double precio = job.getCost();

            System.out.println("precio " + precio);

            jobRepository.save(job);

        } else {
            throw new MyException("No se encontró el trabajo con el ID proporcionado: " + id);
        }
    }

    public void deleteJob(Long id) {

        Optional<Job> result = jobRepository.findById(id);
        Job job = new Job();
        if (result.isPresent()) {
            job = result.get();
            jobRepository.delete(job);
        }

    }

    public void createComment(Long id, Long callification, String comment) throws MyException {
        if (comment == null || comment.isEmpty() || callification < 0 || callification > 5) {
            throw new MyException("El comentario no puede ser nulo o vacío");
        }
        Optional<Job> result = jobRepository.findById(id);
        Job job = new Job();
        if (result.isPresent()) {
            job = result.get();
            job.setCallification(callification);
            job.setComment(comment);
            jobRepository.save(job);
        }
    }

    public List<Job> listJobByProvider(Long idProvider) {

        List<Job> jobs = new ArrayList<>();

        jobs = providerRepository.findByProvider(idProvider);

        return jobs;
    }

    public void validate(String description, Long idSkill, Long idUser, Long idProvider)
            throws MyException {

        if (description == null || description.isEmpty()) {
            throw new MyException("La descripción no puede ser nula o vacía");
        }

        // Validación de IDs
        if (idUser == null || idProvider == null || idSkill == null) {
            throw new MyException("Los IDs no pueden ser nulos");
        }
    }

    public void validate(String comment, Double cost, String description, Long idSkill, Long idUser, Long idProvider)
            throws MyException {

        if (comment == null || comment.isEmpty()) {
            throw new MyException("El comentario no puede ser nulo o vacío");
        }

        // Validación de cost
        if (cost == null || cost < 0) {
            throw new MyException("El costo debe ser un número positivo");
        }

        if (description == null || description.isEmpty()) {
            throw new MyException("La descripción no puede ser nula o vacía");
        }

        // Validación de IDs
        if (idUser == null || idProvider == null || idSkill == null) {
            throw new MyException("Los IDs no pueden ser nulos");
        }
    }

    private void validateDescription(String description, Double cost) throws MyException {
        // Validaciones para la descripción y costo
        if (description == null || description.isEmpty()) {
            throw new MyException("La descripción no puede ser nula o vacía");
        }
        if (cost == null || cost < 0) {
            throw new MyException("El costo debe ser un número positivo");
        }
    }

    // CENSURA DE COMENTARIOS
    public void censorComment(Long id) {
        // completar la lógica necesaria
        Optional<Job> result = jobRepository.findById(id);
        Job job = new Job();
        if (result.isPresent()) {
            job = result.get();
            job.setComment("Este comentario ha sido censurado por contener lenguaje no apropiado");
            jobRepository.save(job);
        }
    }

}
