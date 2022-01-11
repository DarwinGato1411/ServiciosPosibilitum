package com.ec.g2g.repository;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ec.g2g.entidad.Cliente;
import com.ec.g2g.entidad.Factura;

/**
 * Spring Data JPA repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacturaRepository extends CrudRepository<Factura, Integer> {
	
	/*consulta por numero de factura u cliente*/
	Optional<Factura> findByFacNumeroAndIdCliente(String cliCedula, Cliente cliente);
	
	Optional<Factura> findByFacSecuencialUnico(String facSecuencialUnico);
	Optional<Factura> findByTxnId(Integer txnId);
	
//	Factura findFirstByOrderByFacNumeroDescCodTipoambienteAmRuc(String amRuc);

}
