/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.servicio;

import com.ec.entidad.Tipoambiente;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author gato
 */
public class ServicioTipoAmbiente {

    private EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void crear(Tipoambiente tipoambiente) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            em.persist(tipoambiente);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en insertar tipoambiente " + e.getMessage());
        } finally {
            em.close();
        }

    }

    public void eliminar(Tipoambiente tipoambiente) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            em.remove(em.merge(tipoambiente));
            em.getTransaction().commit();

        } catch (Exception e) {
            System.out.println("Error en eliminar  tipoambiente " + e.getMessage());
        } finally {
            em.close();
        }

    }

    public void modificar(Tipoambiente tipoambiente) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            em.merge(tipoambiente);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en insertar tipoambiente " + e.getMessage());
        } finally {
            em.close();
        }

    }

    public Tipoambiente FindALlTipoambiente() {

        List<Tipoambiente> listaTipoambientes = new ArrayList<Tipoambiente>();
        Tipoambiente tipoambiente = null;
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createNamedQuery("Tipoambiente.findAllActivo", Tipoambiente.class);
//           query.setParameter("codigoUsuario", tipoambiente);
            listaTipoambientes = (List<Tipoambiente>) query.getResultList();
            if (listaTipoambientes.size() > 0) {
                tipoambiente = listaTipoambientes.get(0);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta tipoambiente");
        } finally {
            em.close();
        }

        return tipoambiente;
    }

    public Tipoambiente findByAmCodigo(String amCodigo, String RUC) {

        List<Tipoambiente> listaTipoambientes = new ArrayList<Tipoambiente>();
        Tipoambiente tipoambiente = null;
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT t FROM Tipoambiente t WHERE t.amCodigo = :amCodigo AND t.amRuc=:amRuc");
            query.setParameter("amCodigo", amCodigo);
            query.setParameter("amRuc", RUC);
            listaTipoambientes = (List<Tipoambiente>) query.getResultList();
            if (listaTipoambientes.size() > 0) {
                tipoambiente = listaTipoambientes.get(0);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta tipoambiente");
        } finally {
            em.close();
        }

        return tipoambiente;
    }

//    tipo ambiente para autorizacion automatica
    public Tipoambiente findByEstadoEmpresa(String ruc) {

        List<Tipoambiente> listaTipoambientes = new ArrayList<Tipoambiente>();
        Tipoambiente tipoambiente = null;
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT a FROM Tipoambiente a where a.amEstado=:amEstado AND a.amRuc=:amRuc");
            query.setParameter("amEstado", Boolean.TRUE);
            query.setParameter("amRuc", ruc);
            listaTipoambientes = (List<Tipoambiente>) query.getResultList();
            if (listaTipoambientes.size() > 0) {
                tipoambiente = listaTipoambientes.get(0);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta tipoambiente");
        } finally {
            em.close();
        }

        return tipoambiente;
    }

    public List<Tipoambiente> findEmpresas(String amCodigo, String amDescripcion, String amNombreComercial) {

        List<Tipoambiente> listaTipoambientes = new ArrayList<Tipoambiente>();
        Tipoambiente tipoambiente = null;
        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT t FROM Tipoambiente t WHERE t.amCodigo= :amCodigo AND t.amDescripcion =:amDescripcion AND t.amNombreComercial LIKE :amNombreComercial ORDER BY t.amNombreComercial ASC");
            query.setParameter("amCodigo", amCodigo);
            query.setParameter("amDescripcion", amDescripcion);
            query.setParameter("amNombreComercial", "%" + amNombreComercial + "%");
            listaTipoambientes = (List<Tipoambiente>) query.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta tipoambiente");
        } finally {
            em.close();
        }

        return listaTipoambientes;
    }

}
