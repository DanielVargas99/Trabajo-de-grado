package com.example.becasapp;

import java.io.Serializable;

public class listaBecas implements Serializable {

    public String nombre;
    public String descripcion;
    public String pais;
    public String entidad;
    public String tipo;
    public String enlace;
    public String documento;

    public listaBecas(String documento, String nombre, String descripcion, String pais, String entidad, String tipo, String enlace) {
        this.documento = documento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.pais = pais;
        this.entidad = entidad;
        this.tipo = tipo;
        this.enlace = enlace;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
