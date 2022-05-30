/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.servicio;

import com.ec.entidad.FacturaReporte;
import com.ec.entidad.DetalleCompra;
import com.ec.untilitario.CompraPromedio;

import com.ec.untilitario.DetalleCompraUtil;
import com.ec.untilitario.Totales;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author gato
 */
public class ServicioFacturaReporte {

    private EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public List<FacturaReporte> findBetweenFechas(Date inicio, Date fin) {

        List<FacturaReporte> listaFacturaReportes = new ArrayList<FacturaReporte>();
        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT a FROM FacturaReporte a WHERE a.facFecha BETWEEN :inicio and :fin ORDER BY a.facFecha DESC ");
            query.setParameter("inicio", inicio);
            query.setParameter("fin", fin);
            listaFacturaReportes = (List<FacturaReporte>) query.getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta findBetweenFechas reporte facturas "+e.getMessage());
        } finally {
            em.close();
        }

        return listaFacturaReportes;
    }

}
