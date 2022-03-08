/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador.webservices;

/**
 *
 * @author Best
 */
public class RespuestaDocumentos {
    private String descripcion;
    private String estado;

    public RespuestaDocumentos(String descripcion, String estado) {
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public RespuestaDocumentos() {
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
    
}
