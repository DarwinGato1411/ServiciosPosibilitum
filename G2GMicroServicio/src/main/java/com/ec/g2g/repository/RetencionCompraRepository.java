package com.ec.g2g.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ec.g2g.entidad.Cliente;
import com.ec.g2g.entidad.Factura;
import com.ec.g2g.entidad.RetencionCompra;

/**
 * Spring Data JPA repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RetencionCompraRepository extends CrudRepository<RetencionCompra, Integer> {
	/* ultimo secuencial */
	RetencionCompra findFirstByOrderByRcoSecuencialDesc();
	Optional<RetencionCompra> findIdQuickOrRcoSecuencial(Integer idQuick, String rcoSecuencial);

}
