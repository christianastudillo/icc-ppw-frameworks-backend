# Programación y Plataformas Web

# Frameworks Backend: Spring Boot - Proyecto Completo

## Autor

Christian Astudillo

## Descripción

Proyecto backend desarrollado con Spring Boot aplicando arquitectura por
capas:

-   Controllers
-   DTOs
-   Models
-   Services
-   Mappers
-   Entities
-   Repositories
-   PostgreSQL
-   Docker
-   Validaciones Jakarta

------------------------------------------------------------------------

# Práctica 1: Instalación y configuración Spring Boot

## Tecnologías

-   Java 17
-   Spring Boot
-   Gradle
-   Spring Web

## Primer endpoint

Ruta:

GET /api/status

Ejemplo:

``` java
@RestController
public class StatusController {

@GetMapping("/api/status")
public Map<String,Object> status(){

return Map.of(
"service","Spring Boot API",
"status","running"
);

}

}
```

------------------------------------------------------------------------

# Práctica 2: Arquitectura modular

Estructura:

``` text
fundamentos01

├── students
├── products
│
├── controllers
├── services
├── repositories
├── models
├── dtos
├── mapper
└── entity
```

Flujo:

``` text
Cliente
 ↓
Controller
 ↓
DTO
 ↓
Service
 ↓
Repository
 ↓
Database
```

------------------------------------------------------------------------

# Práctica 3: CRUD REST

Se implementaron endpoints:

  Método   Ruta
  -------- --------------------
  GET      /api/students
  POST     /api/students
  PUT      /api/students/{id}
  PATCH    /api/students/{id}
  DELETE   /api/students/{id}

Se utilizaron:

-   DTO para entrada y salida
-   Model para lógica
-   Mapper para conversiones

------------------------------------------------------------------------

# Práctica 4: Servicios

Se eliminó lógica del controlador.

Ahora:

Controller:

``` java
private final UserService service;
```

Service:

``` java
public interface UserService {

List<UserResponseDto> findAll();

UserResponseDto create(CreateUserDto dto);

void delete(Long id);

}
```

Implementación:

``` java
@Service
public class UserServiceImpl implements UserService {


private final UserRepository repository;


public UserServiceImpl(UserRepository repository){

this.repository = repository;

}

}
```

------------------------------------------------------------------------

# Práctica 5: PostgreSQL + Docker + JPA

## Docker

Contenedor:

postgres-dev

Datos:

Usuario: ups

Password: ups123

Base: devdb

Puerto: 5432

Comando:

``` bash
docker exec -it postgres-dev psql -U ups -d devdb
```

------------------------------------------------------------------------

# application.yml

``` yaml
server:
 port: 8080

spring:

 datasource:

  url: jdbc:postgresql://localhost:5432/devdb

  username: ups

  password: ups123


 jpa:

  hibernate:

   ddl-auto: update

  show-sql: true
```

------------------------------------------------------------------------

# Entity

Ejemplo:

``` java
@Entity
@Table(name="products")
public class ProductEntity extends BaseEntity {


@Column(nullable=false)
private String name;


@Column(nullable=false)
private Double price;


@Column(nullable=false)
private Integer stock;


}
```

------------------------------------------------------------------------

# Repository

``` java
@Repository
public interface ProductRepository
extends JpaRepository<ProductEntity,Long>{

}
```

------------------------------------------------------------------------

# Práctica 6: Validación DTO

Dependencia:

``` gradle
implementation("org.springframework.boot:spring-boot-starter-validation")
```

------------------------------------------------------------------------

DTO Producto:

``` java
public class CreateProductDto {


@NotBlank
@Size(min=3,max=150)
private String name;


@NotNull
@Min(0)
private Double price;


@NotNull
@Min(0)
private Integer stock;


}
```

------------------------------------------------------------------------

Controller:

``` java
@PostMapping
public ProductResponseDto create(
@Valid
@RequestBody
CreateProductDto dto){

return service.create(dto);

}
```

------------------------------------------------------------------------

# Validaciones realizadas

Se verificó:

-   nombre obligatorio
-   precio no negativo
-   stock no negativo
-   email válido
-   campos requeridos

------------------------------------------------------------------------

# Pruebas

Producto inválido:

``` json
{
"name":"",
"price":-5,
"stock":-1
}
```

Respuesta:

``` text
400 Bad Request
```

Producto válido:

``` json
{
"name":"Laptop",
"price":850,
"stock":10
}
```

Resultado:

Producto creado correctamente

------------------------------------------------------------------------

# Evidencias finales

Se comprobó:

-   Spring Boot ejecutándose
-   Endpoint status funcionando
-   CRUD students
-   CRUD products
-   Docker PostgreSQL activo
-   Tablas creadas con JPA
-   Validaciones funcionando

------------------------------------------------------------------------

# Conclusión

Se desarrolló una API REST completa utilizando Spring Boot aplicando
buenas prácticas de arquitectura backend.

La aplicación cuenta con separación de responsabilidades, persistencia
real en PostgreSQL, conexión mediante Docker y validación de datos
mediante DTOs.
