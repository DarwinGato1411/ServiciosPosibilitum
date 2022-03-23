/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador.webservices;

/**
 *
 * @author Darwin
 */
import com.ec.controlador.procesar.ProcesarDocumentos;
import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.sf.jasperreports.engine.JRException;

@Path("/autorizar")
public class ServiciosRest {

    @GET
    @Path("/facturas/{ruc}")
    @Produces(MediaType.APPLICATION_JSON)
    public RespuestaDocumentos getFacturas(@PathParam("ruc") String ruc) {
        System.out.println("RUC "+ruc);
        RespuestaDocumentos respuesta = new RespuestaDocumentos("PROCESO CORRECTO", "VALIDO");
        ProcesarDocumentos documentos = new ProcesarDocumentos(ruc);
        try {

            System.out.println("INGRESA LA SERVICIO DE FACTURAS");
            respuesta.setDescripcion(documentos.autorizarEnLote());
        } catch (IOException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        } catch (ClassNotFoundException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        } catch (IllegalAccessException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        } catch (InstantiationException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        } catch (SQLException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        } catch (NamingException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        } catch (JRException e) {
            respuesta.setDescripcion(e.getMessage());
            respuesta.setEstado("ERROR");
        }

        return respuesta;
    }

}
