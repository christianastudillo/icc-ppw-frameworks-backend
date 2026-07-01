package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.BadRequestException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.users.dtos.ChangePasswordDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * Retorna todos los usuarios activos almacenados en PostgreSQL.
     */
    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(UserMapper::toModelFromEntity)
                .map(UserMapper::toResponse)
                .toList();
    }

    /*
     * Busca un usuario activo por id.
     *
     * findByIdAndDeletedFalse ya filtra eliminados,
     * por lo que no es necesario comprobar isDeleted() manualmente.
     */
    @Override
    public UserResponseDto findOne(Long id) {
        UserEntity entity = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserModel model = UserMapper.toModelFromEntity(entity);
        return UserMapper.toResponse(model);
    }

    /*
     * Crea un nuevo usuario y lo persiste en PostgreSQL.
     *
     * Si el email ya está registrado, lanza ConflictException.
     */
    @Override
    public UserResponseDto create(CreateUserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("Email already registered");
        }

        UserModel model = UserMapper.toModelFormDTO(dto);
        UserEntity entity = UserMapper.toEntityFromModel(model);
        UserEntity savedEntity = userRepository.save(entity);
        UserModel savedModel = UserMapper.toModelFromEntity(savedEntity);
        return UserMapper.toResponse(savedModel);
    }

    /*
     * Actualiza completamente un usuario activo.
     *
     * findByIdAndDeletedFalse ya filtra eliminados.
     */
    @Override
    public UserResponseDto update(Long id, UpdateUserDto dto) {
        UserEntity entity = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        UserEntity savedEntity = userRepository.save(entity);
        UserModel model = UserMapper.toModelFromEntity(savedEntity);
        return UserMapper.toResponse(model);
    }

    /*
     * Actualiza parcialmente un usuario activo.
     *
     * Solo actualiza los campos no nulos del DTO.
     * findByIdAndDeletedFalse ya filtra eliminados.
     */
    @Override
    public UserResponseDto partialUpdate(Long id, PartialUpdateUserDto dto) {
        UserEntity entity = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }

        UserEntity savedEntity = userRepository.save(entity);
        UserModel model = UserMapper.toModelFromEntity(savedEntity);
        return UserMapper.toResponse(model);
    }

    /*
     * Elimina lógicamente un usuario por id.
     *
     * findByIdAndDeletedFalse ya filtra eliminados,
     * evitando doble eliminación sin necesidad de comprobar isDeleted().
     */
    @Override
    public void delete(Long id) {
        UserEntity entity = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        entity.setDeleted(true);
        userRepository.save(entity);
    }

    @Override
    public void changePassword(Long id, ChangePasswordDto dto) {
        // Verifica que el usuario exista y no esté eliminado
        UserEntity entity = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Simula verificación de hash; reemplazar con BCrypt en producción
        String expectedHash = "HASH_" + dto.getCurrentPassword();
        if (!expectedHash.equals(entity.getPasswordHash())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }

        entity.setPasswordHash("HASH_" + dto.getNewPassword());
        userRepository.save(entity);
    }
}
