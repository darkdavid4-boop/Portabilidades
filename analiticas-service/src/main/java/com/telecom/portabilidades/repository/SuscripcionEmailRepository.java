package com.telecom.portabilidades.repository;

import com.telecom.portabilidades.model.SuscripcionEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuscripcionEmailRepository extends JpaRepository<SuscripcionEmail, Long> {

    Optional<SuscripcionEmail> findByEmailIgnoreCase(String email);

    Optional<SuscripcionEmail> findByTokenConfirmacion(String tokenConfirmacion);

    Optional<SuscripcionEmail> findByTokenBaja(String tokenBaja);

    List<SuscripcionEmail> findByActivaTrueAndConfirmadaTrue();
}
