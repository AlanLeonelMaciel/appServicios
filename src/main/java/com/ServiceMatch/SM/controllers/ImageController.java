package com.ServiceMatch.SM.controllers;

import static java.nio.file.Files.readAllBytes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ServiceMatch.SM.entities.Image;
import com.ServiceMatch.SM.entities.Provider;
import com.ServiceMatch.SM.services.ImageService;
import com.ServiceMatch.SM.services.ServiceProvider;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/imagen")
public class ImageController {
    
    


    @Autowired
    private ImageService imageService; 

    @GetMapping("/mostrar-imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable Long id) {
        final Image[] m = { new Image() };
        imageService.getProviderById(id).ifPresent(p -> m[0] = p.getImagen());
        if (m[0] == null) {
            try {
                ClassPathResource imageFile = new ClassPathResource("img/trabajador11.jpg");
                InputStream inputStream = imageFile.getInputStream();

                byte[] imageBytes = readAllBytes((Path) inputStream);

                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        byte[] imagenBytes = m[0].getContenido();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(imagenBytes, headers, HttpStatus.OK);

    }
    
      @Autowired
    ServiceProvider serviceProvider;
    
    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> imagenUsuario (@PathVariable Long id){
        Provider usuario = serviceProvider.getOne(id);
        
       byte[] imagen= usuario.getImagen().getContenido();
       
       HttpHeaders headers = new HttpHeaders();
       
       headers.setContentType(MediaType.IMAGE_JPEG);
       
       return new ResponseEntity<>(imagen,headers, HttpStatus.OK); 
    }
}
