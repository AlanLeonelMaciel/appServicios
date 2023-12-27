package com.ServiceMatch.SM.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ServiceMatch.SM.entities.Image;
import com.ServiceMatch.SM.exceptions.MyException;
import com.ServiceMatch.SM.repository.ImageRepository;

@Service
public class ServiceImage {

    @Autowired
    private ImageRepository imagenRepository ;


    @Transactional
    public Image guardarImagen(MultipartFile archivo) throws MyException{

        if(archivo !=null){
            try {
                Image imagen= new Image();
                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                return imagenRepository.save(imagen);
            } catch (Exception e) {

            }

        }

        return null;

    }

    @Transactional
    public Image actualizar(MultipartFile archivo, Long idImagen) throws MyException {
        if(archivo !=null){
            try {
                Image imagen= new Image();

                if(imagen !=null){
                    Optional <Image> respuesta=imagenRepository.findById(idImagen);

                    if(respuesta.isPresent()){
                        imagen=respuesta.get();
                    }

                }

                imagen.setMime(archivo.getContentType());
                imagen.setNombre(archivo.getName());
                imagen.setContenido(archivo.getBytes());
                return imagenRepository.save(imagen);
            } catch (Exception e) {

            }

        }

        return null;


    }


}
