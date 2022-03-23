/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import com.ec.entidad.Guiaremision;
import com.ec.entidad.Tipoambiente;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.untilitario.ArchivoUtils;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

/**
 *
 * @author gato
 */
public class Administracion {

    private List<Tipoambiente> listaEmpresas = new ArrayList<Tipoambiente>();
    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();
    private String amCodigo = "2";
    private String amDescripcion = "PRODUCCION";
    private String amNombreComercial = "";
    private String link = "";

    public Administracion() {
        consultarEmpresas();

    }

    private void consultarEmpresas() {
        listaEmpresas = servicioTipoAmbiente.findEmpresas(amCodigo, amDescripcion, amNombreComercial);
    }

    @Command
    @NotifyChange({"listaEmpresas", "amCodigo", "amDescripcion"})
    public void buscarEmpresas() {

        consultarEmpresas();

    }

    @Command
    @NotifyChange({"listaEmpresas"})
    public void actualizaDatos(@BindingParam("valor") Tipoambiente valor) {

        servicioTipoAmbiente.modificar(valor);
        Clients.showNotification("Puerto actualizado",
                Clients.NOTIFICATION_TYPE_INFO, null, "middle_center", 2000, true);
    }

    @Command
    @NotifyChange({"listaEmpresas", "amCodigo", "amDescripcion"})
    public void activarEmpresa(@BindingParam("valor") Tipoambiente valor) {
        System.out.println("INGRESO AL METODO");

        if (valor.getAmPuerto() != null) {
            String URL = ArchivoUtils.activarQuickPuerto(valor.getAmPuerto());
            if (URL.contains("ERROR")) {
                Executions.sendRedirect("/bienvenido/inicioadmin.zul");
            } else {
                Executions.getCurrent().sendRedirect(URL, "_blank");
            }

        } else {
            Executions.sendRedirect("/bienvenido/inicioadmin.zul");
        }

    }

    public List<Tipoambiente> getListaEmpresas() {
        return listaEmpresas;
    }

    public void setListaEmpresas(List<Tipoambiente> listaEmpresas) {
        this.listaEmpresas = listaEmpresas;
    }

    public String getAmCodigo() {
        return amCodigo;
    }

    public void setAmCodigo(String amCodigo) {
        this.amCodigo = amCodigo;
    }

    public String getAmDescripcion() {
        return amDescripcion;
    }

    public void setAmDescripcion(String amDescripcion) {
        this.amDescripcion = amDescripcion;
    }

    public String getAmNombreComercial() {
        return amNombreComercial;
    }

    public void setAmNombreComercial(String amNombreComercial) {
        this.amNombreComercial = amNombreComercial;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
