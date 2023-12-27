package com.ServiceMatch.SM.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mime;

    private String nombre;


    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] contenido;


    public Image() {
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getMime() {
        return mime;
    }


    public void setMime(String mime) {
        this.mime = mime;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public byte[] getContenido() {
        return contenido;
    }


    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }


}
