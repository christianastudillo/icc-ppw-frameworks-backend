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

# Práctica 8: Relaciones ManyToOne, Foreign Keys y Consultas Relacionales

## Módulo categories

Se agregó el módulo `categories` completo (entidad, DTOs, repositorio,
servicio y controlador), siguiendo la misma arquitectura por capas que
`users` y `products`.

Endpoints:

  Método   Ruta                     Descripción
  -------- ------------------------ -----------------------------------
  GET      /api/categories          Lista categorías activas
  GET      /api/categories/{id}     Obtiene una categoría
  POST     /api/categories          Crea una categoría
  PUT      /api/categories/{id}     Actualiza una categoría
  DELETE   /api/categories/{id}     Elimina lógicamente una categoría

## Relaciones en ProductEntity

``` java
@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private UserEntity owner;

@ManyToOne(optional = false, fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private CategoryEntity category;
```

`ProductEntity` pasa de existir de forma aislada a depender de dos
entidades padre: `UserEntity` (el usuario que registra el producto) y
`CategoryEntity` (la categoría a la que pertenece). Cada `@ManyToOne`
genera una clave foránea en `products` mediante `@JoinColumn` (`user_id`
y `category_id`). `optional = false` impide guardar un producto sin
esas relaciones, y `fetch = FetchType.LAZY` evita cargar el usuario y la
categoría completos hasta que el código accede explícitamente a ellos
(`entity.getOwner()...`), lo que evita consultas innecesarias en
listados grandes.

Como `open-in-view` está deshabilitado en `application.yml`, el acceso a
`owner`/`category` (LAZY) solo funciona si ocurre dentro de la misma
transacción que hizo la consulta. Por eso `ProductServiceImpl` se marcó
con `@Transactional`: mantiene la sesión de Hibernate abierta mientras
se arma el `ProductResponseDto` con los datos anidados.

## Nuevos endpoints de productos

  Método   Ruta                                  Descripción
  -------- ------------------------------------- -----------------------------------
  GET      /api/products/user/{userId}           Lista productos de un usuario
  GET      /api/products/category/{categoryId}   Lista productos de una categoría

## Validaciones agregadas en ProductServiceImpl

-   Usuario inexistente o eliminado → `404 Not Found`
-   Categoría inexistente o eliminada → `404 Not Found`
-   Producto inexistente o eliminado → `404 Not Found`
-   Nombre de producto duplicado → `409 Conflict`

## Captura: estructura de la tabla products en PostgreSQL

_(Pendiente: capturar `\d products` en `docker exec -it postgres-dev psql -U ups -d devdb`,
mostrando las columnas `user_id` y `category_id` como llaves foráneas.)_

## Captura: creación de producto con relaciones (Bruno/Postman)

Petición:

``` json
POST /api/products
{
  "name": "Monitor Gamer",
  "price": 350.0,
  "stock": 8,
  "userId": 4,
  "categoryId": 2
}
```

Respuesta verificada:

``` json
{
  "id": 5,
  "name": "Monitor Gamer",
  "price": 350.0,
  "stock": 8,
  "owner": {
    "id": 4,
    "name": "Ana Prueba",
    "email": "ana.prueba@ups.edu.ec"
  },
  "category": {
    "id": 2,
    "name": "Electronicos",
    "description": "Dispositivos electronicos"
  },
  "createdAt": "2026-07-01T10:46:23.086061",
  "updatedAt": null
}
```

_(Pendiente: capturar esta misma respuesta desde Bruno/Postman.)_

## Captura: consulta de productos por categoría

``` text
GET /api/products/category/2
```

_(Pendiente: capturar desde Bruno/Postman.)_

------------------------------------------------------------------------

# Conclusión

Se desarrolló una API REST completa utilizando Spring Boot aplicando
buenas prácticas de arquitectura backend.

La aplicación cuenta con separación de responsabilidades, persistencia
real en PostgreSQL, conexión mediante Docker y validación de datos
mediante DTOs.
