package ec.edu.ups.icc.fundamentos01.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByIdAndDeletedFalse(Long id);

    Optional<UserEntity> findByIdAndDeleted(Long id, boolean deleted);

    Optional<UserEntity> findByNameAndId(String name, Long id);

    // Buscar usuario por email (usado en login)
    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    boolean existsByIdAndDeletedFalse(Long id);

    // Verificar si email ya está registrado (usado en registro)
    boolean existsByEmail(String email);

}
