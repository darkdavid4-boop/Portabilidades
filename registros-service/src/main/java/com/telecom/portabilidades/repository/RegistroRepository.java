package com.telecom.portabilidades.repository;

import com.telecom.portabilidades.model.RegistroTelecomunicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroRepository extends JpaRepository<RegistroTelecomunicacion, Long>, JpaSpecificationExecutor<RegistroTelecomunicacion> {

    boolean existsByOperadorIgnoreCase(String operador);

    Page<RegistroTelecomunicacion> findByOperadorIgnoreCase(String operador, Pageable pageable);

    Page<RegistroTelecomunicacion> findByOperadorIgnoreCaseAndMesStartingWith(String operador, String año, Pageable pageable);

    Page<RegistroTelecomunicacion> findByOperadorIgnoreCaseAndMes(String operador, String mes, Pageable pageable);

    Page<RegistroTelecomunicacion> findByMes(String mes, Pageable pageable);

    Page<RegistroTelecomunicacion> findByTecnologiaAccesoIgnoreCase(String tecnologiaAcceso, Pageable pageable);

    List<RegistroTelecomunicacion> findByConceptoAndMes(String concepto, String mes);

    List<RegistroTelecomunicacion> findByConceptoAndMesStartingWith(String concepto, String año);

    List<RegistroTelecomunicacion> findByServicioAndMes(String servicio, String mes);

    List<RegistroTelecomunicacion> findByMesStartingWith(String año);

    @Query("SELECT DISTINCT r.operador FROM RegistroTelecomunicacion r WHERE r.operador IS NOT NULL AND r.operador != 'N/A'")
    List<String> findDistinctOperadores();

    @Query("SELECT DISTINCT r.mes FROM RegistroTelecomunicacion r WHERE r.mes IS NOT NULL ORDER BY r.mes DESC")
    List<String> findDistinctMeses();

    @Query("SELECT MIN(r.mes) FROM RegistroTelecomunicacion r")
    String findMesMinimo();

    @Query("SELECT MAX(r.mes) FROM RegistroTelecomunicacion r")
    String findMesMaximo();
}
