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

# Práctica 7: Control Centralizado de Errores y Excepciones

## Problema que resuelve

Antes de esta práctica, cada excepción sin capturar (`IllegalStateException`,
`NullPointerException`, etc.) llegaba al cliente como una respuesta técnica
de Spring Boot, sin un formato uniforme y sin poder distinguir un `404` de
un `409` o un `400`. Se centraliza el manejo de errores para que, sin
importar en qué capa ocurra el problema (DTO, servicio o repositorio), el
cliente siempre reciba la misma estructura de respuesta.

## Excepciones personalizadas

Archivo base, `core/exceptions/base/ApplicationException.java`:

``` java
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
```

Excepciones de dominio en `core/exceptions/domain/`, cada una fija su
propio código HTTP:

``` java
public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}

public class ConflictException extends ApplicationException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}

public class BadRequestException extends ApplicationException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
```

Se usan en los servicios según el tipo de error:

-   `NotFoundException` → usuario, categoría o producto inexistente/eliminado.
-   `ConflictException` → email o nombre de producto/categoría duplicado.
-   `BadRequestException` → regla de negocio incumplida (por ejemplo, un
    rango de precio inválido en los filtros).

## Formato uniforme de error: `ErrorResponse`

``` java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
}
```

`@JsonInclude(NON_NULL)` hace que `details` no aparezca en el JSON cuando
el error no viene de una validación por campos (por ejemplo, un `404`).

## `GlobalExceptionHandler`

Archivo `core/exceptions/handler/GlobalExceptionHandler.java`, anotado con
`@RestControllerAdvice` para aplicarse a todos los controladores:

``` java
@ExceptionHandler(ApplicationException.class)
public ResponseEntity<ErrorResponse> handleApplicationException(
        ApplicationException ex, HttpServletRequest request) {
    ErrorResponse response = new ErrorResponse(
            ex.getStatus(), ex.getMessage(), request.getRequestURI());
    return ResponseEntity.status(ex.getStatus()).body(response);
}

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", request.getRequestURI(), errors);
    return ResponseEntity.badRequest().body(response);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleUnexpectedException(
        Exception ex, HttpServletRequest request) {
    ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
}
```

(El manejo de `BindException` para filtros con `@ModelAttribute` se agregó
en la práctica 9 y se documenta en esa sección.)

## Pruebas realizadas

Usuario inexistente:

``` text
GET /api/users/999
```

``` json
{
  "timestamp": "2026-07-01T10:20:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/users/999"
}
```

Email duplicado al crear usuario:

``` json
POST /api/users
{
  "name": "Juan Repetido",
  "email": "juan@ups.edu.ec",
  "password": "12345678"
}
```

``` json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already registered",
  "path": "/api/users"
}
```

Cuerpo inválido (`@Valid` sobre `@RequestBody`):

``` json
POST /api/users
{
  "name": "",
  "email": "correo-invalido",
  "password": "123"
}
```

``` json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada inválidos",
  "path": "/api/users",
  "details": {
    "name": "El nombre es obligatorio",
    "email": "Debe ingresar un email válido",
    "password": "La contraseña debe tener al menos 8 caracteres"
  }
}
```

## Explicación breve

Antes de centralizar el manejo de errores, cada controlador o servicio
tendría que armar su propia respuesta de error con `try/catch`, lo que
produce mensajes inconsistentes y expone detalles técnicos innecesarios
(stacktraces, nombres de clases, mensajes de PostgreSQL). Con
`@RestControllerAdvice`, los servicios solo lanzan la excepción que
corresponde al problema (`NotFoundException`, `ConflictException`,
`BadRequestException`) y es el `GlobalExceptionHandler` quien decide,
en un único lugar, cómo traducir esa excepción a una respuesta HTTP con
el formato de `ErrorResponse`. Esto mantiene los controladores y
servicios enfocados en su propia responsabilidad y garantiza que
cualquier endpoint nuevo (incluidos los agregados en las prácticas 8 y 9)
responda errores con la misma estructura sin escribir código adicional.

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

# Práctica 9: Request Parameters, Consultas Relacionadas y Filtrado con JPA

## Objetivo

Consultar productos desde el contexto semántico de `users` y `categories`
(`/api/users/{id}/products`, `/api/categories/{id}/products`) aplicando
filtros opcionales por query params (`name`, `minPrice`, `maxPrice`,
`categoryId`, `userId`), en lugar de usar los endpoints técnicos
`/api/products/user/{userId}` y `/api/products/category/{categoryId}`
agregados en la Práctica 8.

## Decisión de diseño: un solo DTO de filtros

La guía propone dos DTOs (`ProductFilterByUserDto` para el contexto de
usuario y, de forma implícita, `ProductFilterByCategoryDto` para el
contexto de categoría). En este proyecto ambos casos se unificaron en un
único `ProductFilterDto` con los cinco campos (`name`, `minPrice`,
`maxPrice`, `categoryId`, `userId`), reutilizado por ambos controladores.
Cada endpoint simplemente ignora el campo que no le corresponde como
filtro cruzado (p. ej. `categoryId` cuando se consulta desde
`/users/{id}/products`, o `userId` cuando se consulta desde
`/categories/{id}/products`), evitando duplicar la misma clase de
validación dos veces.

## ProductFilterDto

`src/main/java/ec/edu/ups/icc/fundamentos01/products/dtos/ProductFilterDto.java`

``` java
public class ProductFilterDto {

    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String name;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio mínimo no puede ser negativo")
    private Double minPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "El precio máximo no puede ser negativo")
    private Double maxPrice;

    @Min(value = 1, message = "El ID de categoría debe ser mayor a 0")
    private Long categoryId;

    @Min(value = 1, message = "El ID de usuario debe ser mayor a 0")
    private Long userId;

    public boolean hasValidPriceRange() {
        if (minPrice != null && maxPrice != null) {
            return maxPrice >= minPrice;
        }
        return true;
    }

    // Getters y setters
}
```

## ProductRepository: consultas con filtros opcionales

``` java
@Query("""
        SELECT p
        FROM ProductEntity p
        WHERE p.deleted = false
          AND p.owner.id = :userId
          AND p.owner.deleted = false
          AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:categoryId IS NULL OR p.category.deleted = false)
        """)
List<ProductEntity> findByOwnerIdWithFilters(
        @Param("userId") Long userId,
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("categoryId") Long categoryId);

@Query("""
        SELECT p
        FROM ProductEntity p
        WHERE p.deleted = false
          AND p.category.id = :categoryId
          AND p.category.deleted = false
          AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', COALESCE(:name, ''), '%')))
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
          AND (:userId IS NULL OR p.owner.id = :userId)
          AND (:userId IS NULL OR p.owner.deleted = false)
        """)
List<ProductEntity> findByCategoryIdWithFilters(
        @Param("categoryId") Long categoryId,
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("userId") Long userId);
```

Cada condición sigue el patrón `(:param IS NULL OR <filtro>)`: si el
query param no llega, la condición se evalúa `true` y no restringe la
consulta; si llega, se aplica el filtro sobre la tabla `products` sin
necesidad de traer registros a memoria para filtrarlos en Java.

## GlobalExceptionHandler: BindException

Los filtros llegan por `@ModelAttribute` en vez de `@RequestBody`, así
que una validación fallida no lanza `MethodArgumentNotValidException`
sino `BindException` (superclase de la anterior). Se agregó un handler
dedicado que devuelve el mismo formato estándar de error:

``` java
@ExceptionHandler(BindException.class)
public ResponseEntity<ErrorResponse> handleBindException(
        BindException ex,
        HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
            .getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Parámetros de consulta inválidos",
            request.getRequestURI(),
            errors);

    return ResponseEntity.badRequest().body(response);
}
```

## Endpoint semántico en UserController

``` java
@GetMapping("/{id}/products")
public List<ProductResponseDto> findProductsByUser(
        @PathVariable Long id,
        @Valid @ModelAttribute ProductFilterDto filters) {
    return productService.findByUserIdWithFilters(id, filters);
}
```

## Endpoint semántico en CategoriesController

``` java
@GetMapping("/{id}/products")
public List<ProductResponseDto> findProductsByCategory(
        @PathVariable Long id,
        @Valid @ModelAttribute ProductFilterDto filters) {
    return productService.findByCategoryIdWithFilters(id, filters);
}
```

## Endpoints disponibles

  Método   Ruta                                                                 Descripción
  -------- -------------------------------------------------------------------- -----------------------------------
  GET      `/api/users/{id}/products`                                          Lista productos de un usuario
  GET      `/api/users/{id}/products?name=...`                                 Filtra por nombre
  GET      `/api/users/{id}/products?minPrice=...&maxPrice=...`                Filtra por rango de precio
  GET      `/api/users/{id}/products?categoryId=...`                           Filtra por categoría
  GET      `/api/categories/{id}/products`                                     Lista productos de una categoría
  GET      `/api/categories/{id}/products?userId=...`                          Filtra por usuario
  GET      `/api/categories/{id}/products?name=...&minPrice=...`               Combina filtros

## Evidencias (pruebas reales contra PostgreSQL vía Docker)

Categoría `1 - General` con productos existentes (`Notebook`, `Laptop Asus`, `Teclado`, todos de `userId=1`).

Filtro por nombre desde el contexto de categoría:

``` text
GET /api/categories/1/products?name=Laptop
```

``` json
[
  {
    "id": 3,
    "name": "Laptop Asus",
    "price": 1200.0,
    "stock": 5,
    "category": { "id": 1, "name": "General", "description": "Categoria general de migracion" },
    "owner": { "id": 1, "name": "Christian Nuevo Nombre", "email": "christian.actualizado@ups.edu.ec" }
  }
]
```

Filtro por rango de precio desde el contexto de categoría:

``` text
GET /api/categories/1/products?minPrice=1000&maxPrice=1300
```

Resultado: `Notebook` (1200.0) y `Laptop Asus` (1200.0), ambos dentro del rango.

Filtro combinado usuario + categoría:

``` text
GET /api/categories/1/products?userId=1
```

Devuelve los 3 productos de la categoría 1, todos pertenecientes al usuario 1.

Error por usuario inexistente:

``` text
GET /api/users/999/products
```

``` json
{
  "timestamp": "2026-07-02T10:34:14.4444468",
  "status": 404,
  "error": "Not Found",
  "message": "User not found",
  "path": "/api/users/999/products"
}
```

Error por categoría inexistente:

``` text
GET /api/categories/999/products
```

``` json
{
  "status": 404,
  "error": "Not Found",
  "message": "Category not found",
  "path": "/api/categories/999/products"
}
```

Error por rango de precio inválido:

``` text
GET /api/users/4/products?minPrice=1500&maxPrice=500
```

``` json
{
  "status": 400,
  "error": "Bad Request",
  "message": "El precio máximo debe ser mayor o igual al precio mínimo",
  "path": "/api/users/4/products"
}
```

Error por parámetro inválido (`minPrice` negativo), validado por
`@ModelAttribute` + `@Valid` y capturado por el handler de bind:

``` text
GET /api/users/4/products?minPrice=-5
```

``` json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada inválidos",
  "path": "/api/users/4/products",
  "details": {
    "minPrice": "El precio mínimo no puede ser negativo"
  }
}
```

Filtro por `categoryId` inexistente dentro del contexto de usuario
(valida la existencia del filtro, no solo del recurso principal):

``` text
GET /api/users/4/products?categoryId=999
```

``` json
{
  "status": 404,
  "error": "Not Found",
  "message": "Category not found",
  "path": "/api/users/4/products"
}
```

## Colección Bruno

Se agregaron las peticiones de esta práctica en `bruno/User/07` a `14` y
`bruno/Categories/04` a `07`, cubriendo listado sin filtros, filtro por
nombre, por rango de precio, por categoría/usuario, filtros combinados y
los tres casos de error (usuario inexistente, categoría inexistente y
rango de precio inválido).

## Explicación breve

**¿Por qué se usa `ProductRepository` para consultar productos aunque el
endpoint esté dentro del contexto `/users/{id}/products`?**

Porque el recurso que se está leyendo sigue siendo `products`, no
`users`. La URL solo describe el contexto semántico desde el cual se
origina la consulta (los productos *de* un usuario), pero los datos que
se necesitan, filtran y paginan viven en la tabla `products`. Si en
lugar de eso se navegara la relación desde `UserEntity` (por ejemplo
agregando `@OneToMany(mappedBy = "owner") Set<ProductEntity> products`),
Hibernate traería la colección completa a memoria y cualquier filtro
(`name`, `minPrice`, `categoryId`) tendría que aplicarse con streams en
Java en vez de con SQL, perdiendo la capacidad del motor de base de
datos de indexar y optimizar la consulta. Consultar directamente desde
`ProductRepository` con `@Query` y parámetros opcionales mantiene el
filtrado a nivel de base de datos, evita cargar registros de más y no
obliga a acoplar `UserEntity` con una colección que no siempre se
necesita.

------------------------------------------------------------------------

# Conclusión

Se desarrolló una API REST completa utilizando Spring Boot aplicando
buenas prácticas de arquitectura backend.

La aplicación cuenta con separación de responsabilidades, persistencia
real en PostgreSQL, conexión mediante Docker y validación de datos
mediante DTOs.
