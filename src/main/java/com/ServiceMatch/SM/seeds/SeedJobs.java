package com.ServiceMatch.SM.seeds;

import com.ServiceMatch.SM.entities.Job;
import com.ServiceMatch.SM.enums.JobStatusEnum;
import com.ServiceMatch.SM.repository.JobRepository;
import com.ServiceMatch.SM.repository.ProviderRepository;
import com.ServiceMatch.SM.repository.SkillRepository;
import com.ServiceMatch.SM.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

@Component
public class SeedJobs implements ApplicationRunner {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final JobRepository jobRepository;
    @Autowired
    private final SkillRepository skillRepository;
    @Autowired
    private final ProviderRepository providerRepository;
    public SeedJobs(ProviderRepository providerRepository,JobRepository jobRepository, SkillRepository skillRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;

    }
    
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // Verificar si ya existen jobs en la base de datos

        if (jobRepository.count() != 0) {
            return;
        }

        job1Create();
        job2Create();
        
    }

    private void job1Create() {
        // Crear job1
        Job nuevoJob = new Job();
        nuevoJob.setCallification(5L);
        nuevoJob.setComment("Trabajo sencillo de Plomería bien hecho");
        nuevoJob.setStartDate(new Date()); // Establece la fecha de inicio como la fecha actual
        nuevoJob.setEndDate(null); // Establece la fecha de fin como nula (puede cambiar según sea necesario)
        nuevoJob.setCost(10000.0);
        nuevoJob.setDescription("Descripción del trabajo");
        nuevoJob.setJobStatus(JobStatusEnum.PENDING);
        nuevoJob.setSkill(skillRepository.getById(1L));
        nuevoJob.setAppUser(userRepository.getReferenceById(4L));
        nuevoJob.setProvider(providerRepository.getReferenceById(2L));

        // Guarda el nuevo Job en la base de datos
        jobRepository.save(nuevoJob); // Utiliza el método save del repositorio correspondiente
    }

    private void job2Create() {
        // Crear job1
        Job nuevoJob = new Job();
        nuevoJob.setCallification(5L);
        nuevoJob.setComment("Detalle de Plomería bien hecho");
        nuevoJob.setStartDate(new Date()); // Establece la fecha de inicio como la fecha actual
        nuevoJob.setEndDate(null); // Establece la fecha de fin como nula (puede cambiar según sea necesario)
        nuevoJob.setCost(12000.0);
        nuevoJob.setDescription("Plomeria arreglo sencillo");
        nuevoJob.setJobStatus(JobStatusEnum.PENDING);
        nuevoJob.setSkill(skillRepository.getById(1L));
        nuevoJob.setAppUser(userRepository.getReferenceById(4L));
        nuevoJob.setProvider(providerRepository.getReferenceById(2L));

        // Guarda el nuevo Job en la base de datos
        jobRepository.save(nuevoJob); // Utiliza el método save del repositorio correspondiente
    }


}





