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

# Práctica 10: Paginación con Page y Slice

Hasta la práctica anterior, cualquier endpoint de listado (`GET /api/products`,
`GET /api/categories/{id}/products`) devolvía la colección completa en un
solo `List<ProductResponseDto>`. Mientras la base de datos tenía pocos
registros esto no se notaba, pero es un problema real de rendimiento: cada
petición trae todos los productos, con su `owner` y sus `categories`
anidados, sin importar si el cliente necesita 5 o 5000. En esta práctica
resolví eso agregando paginación real a nivel de repositorio (no en
memoria), usando dos estrategias que ofrece Spring Data: `Page` y `Slice`.

## Qué implementé

Agregué `PaginationDto` como el DTO que recibo por query params
(`page`, `size`, `sortBy`, `direction`), con sus propias validaciones
(`@Min`, `@Max`) para que un `page` negativo o un `size` fuera de rango
no llegue nunca al repositorio. En `ProductServiceImpl` armé un método
privado `createPageable(PaginationDto)` que construye el `Pageable` y
valida que el campo de ordenamiento (`sortBy`) esté en una lista blanca
de columnas permitidas, para no permitir ordenar por relaciones que no
están preparadas para eso.

Con eso agregué cuatro endpoints nuevos:

| Método | Ruta | Descripción |
| ------ | ---- | ----------- |
| GET | `/api/products/page` | Productos activos con `Page` (incluye metadatos completos) |
| GET | `/api/products/slice` | Productos activos con `Slice` (más liviano, sin contar el total) |
| GET | `/api/categories/{id}/products/page` | Productos de una categoría, con filtros + `Page` |
| GET | `/api/categories/{id}/products/slice` | Productos de una categoría, con filtros + `Slice` |

## Evidencias

> Las peticiones y respuestas de esta sección las armé a partir de mis
> propios DTOs y del `PageImpl`/`SliceImpl` reales que devuelve Spring
> Data, para que la forma y los nombres de campo sean exactos. No pude
> levantar el servidor en este entorno para ejecutar la llamada real
> (no tengo acceso a Maven Central ni a Postgres desde aquí), así que
> cada bloque queda marcado como **(Pendiente: reemplazar con tu propia
> captura real desde Bruno)** hasta que yo mismo corra la colección
> `Proyecto-Bruno/Paginacion` y pegue la respuesta verdadera.

### Captura de respuesta con Page

```txt
GET /api/products/page?page=0&size=5
```

```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop",
      "price": 850.0,
      "stock": 10,
      "owner": { "id": 1, "name": "Christian Astu", "email": "chro@example.com" },
      "categories": [
        { "id": 1, "name": "Computadoras", "description": "Equipos de cómputo" }
      ],
      "createdAt": "2026-07-01T10:46:23.086061",
      "updatedAt": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": { "sorted": true, "unsorted": false, "empty": false },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 12,
  "totalPages": 3,
  "number": 0,
  "size": 5,
  "first": true,
  "last": false,
  "numberOfElements": 5,
  "empty": false
}
```

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de respuesta con Slice

```txt
GET /api/products/slice?page=0&size=5
```

```json
{
  "content": [
    {
      "id": 1,
      "name": "Laptop",
      "price": 850.0,
      "stock": 10,
      "owner": { "id": 1, "name": "Christian Astu", "email": "chro@example.com" },
      "categories": [
        { "id": 1, "name": "Computadoras", "description": "Equipos de cómputo" }
      ],
      "createdAt": "2026-07-01T10:46:23.086061",
      "updatedAt": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "number": 0,
  "size": 5,
  "numberOfElements": 5,
  "first": true,
  "last": false,
  "empty": false
}
```

Como se puede ver, `totalElements` y `totalPages` **no aparecen** en la
respuesta de `Slice`: eso es justamente lo que la hace más liviana, porque
Spring Data no ejecuta el `COUNT(*)` adicional que sí necesita `Page`.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de error por paginación inválida

```txt
GET /api/products/page?page=-1&size=0
```

```json
{
  "timestamp": "2026-07-10T09:12:04.1122334",
  "status": 400,
  "error": "Bad Request",
  "message": "Parámetros de consulta inválidos",
  "path": "/api/products/page",
  "details": {
    "page": "La página debe ser mayor o igual a 0",
    "size": "El tamaño debe ser mayor o igual a 1"
  }
}
```

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de endpoint de categoría paginado (Page)

```txt
GET /api/categories/2/products/page?page=0&size=5
```

Debe evidenciar productos filtrados por la categoría `2`, con los mismos
metadatos de `Page` que el endpoint general.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de endpoint de categoría paginado (Slice)

```txt
GET /api/categories/2/products/slice?page=0&size=5
```

Debe evidenciar productos filtrados por la categoría `2`, sin
`totalElements` ni `totalPages`.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

## Colección Bruno

Estos cuatro casos ya están armados en `Proyecto-Bruno/Paginacion`
(`DatosCompletos`, `Slice/Slice`, `Slice/Slice1`, `Slice/Slice2`,
`Errores/Error1` a `Error3`, `PaginacionCategorias/PC1` y `PC2`,
`Metadatos/AScendente` y `Descente`). Solo me falta correrlos con el
servidor levantado y pegar las respuestas reales en los bloques de
arriba.

## Explicación breve

**¿Cuál es la diferencia entre `Page` y `Slice`?**

Las dos traen resultados paginados, pero `Page` además calcula
`totalElements` y `totalPages`, lo que internamente obliga a Spring Data
a ejecutar una segunda consulta `COUNT(*)` sobre la tabla. `Slice` se
salta ese conteo: solo pide `size + 1` registros para saber si existe
una página siguiente (`hasNext()`), y con eso arma `first`/`last` sin
tocar el total. Por eso uso `Page` cuando de verdad necesito mostrar
"página 2 de 8" en el frontend, y `Slice` cuando solo necesito scroll
infinito o "cargar más", porque es más barato para la base de datos.

**¿Por qué la paginación debe aplicarse en el repositorio y no después
de traer todos los datos en memoria?**

Porque si trajera todos los productos con `findAll()` y luego los
recortara con `.subList()` en Java, la base de datos seguiría haciendo
el trabajo pesado de leer y transportar cada fila, cada `owner` y cada
`category` por la red, aunque el cliente solo pida 5 resultados. Eso no
escala: con 10 productos no se nota, pero con 100 000 sería un endpoint
lentísimo y un consumo de memoria innecesario en el propio backend.
Pasarle el `Pageable` directamente a `ProductRepository` hace que sea
PostgreSQL quien resuelva el `LIMIT`/`OFFSET` (o el equivalente con
`keyset`), que es exactamente para lo que está optimizado un motor de
base de datos.

------------------------------------------------------------------------

# Práctica 11: Autenticación y Autorización con JWT

Todo lo anterior (CRUD de productos, categorías, filtros, paginación)
estaba completamente abierto: cualquiera que conociera la URL podía
crear, editar o borrar cualquier recurso. En esta práctica le puse una
puerta de entrada real a la API con autenticación basada en JWT
(JSON Web Token), siguiendo el patrón *stateless* que se espera de una
API REST: el servidor no guarda sesión, toda la identidad del usuario
viaja firmada dentro del propio token en cada petición.

## Qué implementé

- **`RoleEntity`** y el enum `RoleName` (`ROLE_USER`, `ROLE_ADMIN`),
  inicializados automáticamente al arrancar la app con
  `SecurityDataInitializer`.
- **`UserEntity`** actualizado con relación `ManyToMany` hacia los roles
  (tabla intermedia `user_roles`) y campo `passwordHash`.
- **`RegisterRequestDto`** y **`LoginRequestDto`**, con sus propias
  validaciones (`@Email`, contraseña con mínimo 8 caracteres y al menos
  una mayúscula/minúscula/número).
- **`JwtUtil`**, que genera y valida el token firmado (`jjwt`), con
  tiempo de expiración configurable desde `application.yml`.
- **`JwtAuthenticationFilter`**, que intercepta cada request, extrae el
  header `Authorization: Bearer <token>`, lo valida y deja al usuario
  autenticado en el `SecurityContext`.
- **`JwtAuthenticationEntryPoint`**, que responde `401` con el formato
  `ErrorResponse` cuando no hay token o es inválido — sin este
  componente, Spring Security devolvía su propia página de error por
  defecto en vez de JSON.
- **`SecurityConfig`**, con `/auth/**`, `/status/**` y `/actuator/**`
  públicos, y `.anyRequest().authenticated()` para todo lo demás.
- **`AuthService`** y **`AuthController`**, con los endpoints
  `POST /api/auth/register` y `POST /api/auth/login`.

## Evidencias

> Igual que en la práctica anterior, no pude levantar el servidor en
> este entorno (sin acceso a Maven Central ni a una base de datos
> Postgres desde aquí), así que las respuestas de abajo son la forma
> exacta que arma `AuthResponseDto` y `JwtAuthenticationEntryPoint` en mi
> código, no una ejecución real todavía.

### Captura de registro exitoso

```txt
POST /api/auth/register
```

```json
{
  "name": "Usuario A",
  "email": "usera@ups.edu.ec",
  "password": "Password123"
}
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyYUB1cHMuZWR1LmVjIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTc1MjE1MDAwMCwiZXhwIjoxNzUyMTUxODAwfQ.firma-real-generada-por-jjwt",
  "type": "Bearer",
  "userId": 6,
  "name": "Usuario A",
  "email": "usera@ups.edu.ec",
  "roles": ["ROLE_USER"]
}
```

Status esperado: `201 Created`. Confirma token generado y `ROLE_USER`
asignado automáticamente (así lo hace `AuthService.register()`, ningún
usuario nuevo puede auto-asignarse `ROLE_ADMIN`).

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de login exitoso

```txt
POST /api/auth/login
```

```json
{
  "email": "usera@ups.edu.ec",
  "password": "Password123"
}
```

Respuesta con el mismo formato de arriba (`200 OK`), con un token nuevo
y los roles reales del usuario.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de endpoint protegido sin token

```txt
GET /api/products/page?page=0&size=5
```

```json
{
  "timestamp": "2026-07-10T09:20:11.5566778",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token de autenticación inválido o no proporcionado. Debe incluir un token válido en el header Authorization: Bearer <token>",
  "path": "/api/products/page"
}
```

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de endpoint protegido con token

```http
GET /api/products/page?page=0&size=5
Authorization: Bearer <token>
```

Status esperado: `200 OK`, con el mismo cuerpo de `Page<ProductResponseDto>`
documentado en la Práctica 10.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

## Colección Bruno

Register, Login y las pruebas con/sin token ya están armadas en
`bruno/auth` (`Register.bru`, `Login.bru`, `EndPointSinTokken.bru`) y en
`Proyecto-Bruno/Practica11/Auth` (con el script que guarda el token
automáticamente en `{{token}}` después de cada login).

------------------------------------------------------------------------

# Práctica 12: Roles y @PreAuthorize

Con JWT ya resuelto quedaba una pregunta abierta: cualquier usuario
autenticado, sin importar su rol, podía llegar a cualquier endpoint. En
esta práctica agregué una segunda capa de seguridad — autorización por
rol — usando `@PreAuthorize` de Spring Security a nivel de método.

## Qué implementé

- Confirmé `@EnableMethodSecurity(prePostEnabled = true)` en
  `SecurityConfig` (necesario para que `@PreAuthorize` funcione).
- Agregué `@PreAuthorize("hasRole('ADMIN')")` sobre
  `ProductController.findAll()` (`GET /api/products`), que es el único
  endpoint que devuelve la lista completa sin paginar y sin filtrar por
  dueño — por eso lo until reservé solo para administradores.
- Actualicé `GlobalExceptionHandler` con tres manejadores nuevos:
  `AuthorizationDeniedException` (la que lanza `@PreAuthorize` en
  Spring Security 6.x) y `AccessDeniedException` (la que se usa desde
  código propio, incluida la de ownership en la Práctica 13) devuelven
  `403`; `AuthenticationException` devuelve `401`. Sin estos
  manejadores, un acceso denegado por rol devolvía `500` en vez de
  `403`.

## Pendiente detectado

La guía que subiste para esta práctica también pide un endpoint
`GET /api/users/me` (con `CurrentUserController` y
`CurrentUserResponseDto`) que devuelva `id`, `name`, `email` y `roles`
del usuario autenticado usando `@AuthenticationPrincipal`. Revisé el
repositorio y ese controlador **todavía no existe** — solo implementé
la parte de `@PreAuthorize` sobre `findAll()` en una versión anterior de
esta guía. Dejo la sección de captura correspondiente marcada como
pendiente de implementación, no solo de captura.

## Evidencias

> Mismo caso que las prácticas anteriores: no ejecuté el servidor real
> en este entorno, así que dejo la forma exacta de la respuesta y el
> aviso de reemplazo.

### Captura de usuario autenticado — **pendiente de implementar**

```txt
GET /api/users/me
```

Este endpoint aún no existe en el código (`CurrentUserController` no
está creado). Falta implementarlo antes de poder capturar esta
evidencia.

### Captura de acceso denegado por rol

```txt
GET /api/products
Authorization: Bearer <token-ROLE_USER>
```

```json
{
  "timestamp": "2026-07-10T09:25:40.9988776",
  "status": 403,
  "error": "Forbidden",
  "message": "No tienes permisos para acceder a este recurso",
  "path": "/api/products"
}
```

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de acceso permitido por rol ADMIN

```txt
GET /api/products
Authorization: Bearer <token-ROLE_ADMIN>
```

Status esperado: `200 OK`, con la lista completa de productos activos
(`List<ProductResponseDto>`).

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

## Colección Bruno

Armé `bruno/Product/01 - ListarProductoAll.bru` (con el header
`Authorization: Bearer {{token}}`) y
`bruno/Product/11 - ListarProductoAllSinToken.bru` para los tres casos:
`403` con usuario normal, `200` con administrador y `401` sin token.

## Explicación breve

**¿Cuál es la diferencia entre autenticación y autorización?**

Autenticación responde *¿quién eres?*: es lo que resolví en la Práctica
11 validando el JWT y confirmando que el usuario existe y su contraseña
es correcta. Autorización responde *¿qué puedes hacer?*: una vez que sé
quién eres, todavía tengo que decidir si tienes permiso para la acción
puntual que estás pidiendo. Por eso son dos pasos distintos y en orden:
primero pasa por `JwtAuthenticationFilter` (autenticación, devuelve
`401` si falla) y solo después llega a `@PreAuthorize` (autorización,
devuelve `403` si falla).

**¿Por qué `GET /api/products` debe ser solo para ADMIN, mientras
`GET /api/products/page` puede ser consumido por cualquier usuario
autenticado?**

Porque no son el mismo tipo de consulta. `/api/products` devuelve
*todos* los productos activos del sistema sin paginar y sin ningún
filtro de dueño — en la práctica, expone datos de todos los usuarios en
una sola respuesta, lo cual es información operativa que solo le sirve
a un administrador. `/api/products/page` en cambio está pensado como el
endpoint normal de navegación para cualquier usuario: viene paginado,
no dispara un `SELECT *` gigante, y no revela nada que un usuario común
no debería poder consultar. Restringir el primero y dejar abierto el
segundo es justamente aplicar el principio de menor privilegio: cada
rol solo llega hasta donde necesita.

------------------------------------------------------------------------

# Práctica 13: Validación de Ownership

Con roles ya funcionando faltaba resolver la última pregunta de
seguridad: que un usuario con `ROLE_USER` no pueda modificar o borrar
productos que no le pertenecen, aunque esté perfectamente autenticado y
autorizado a *usar* el endpoint. A esto se le llama *ownership*, y a
diferencia de la Práctica 12 (que se resuelve antes de entrar al
método, con `@PreAuthorize`), esta validación necesita conocer el dato
concreto — el producto — así que vive dentro del servicio.

## Qué implementé

- Eliminé el campo `userId` de `CreateProductDto`: ya no confío en lo
  que mande el cliente en el body para decidir quién es el dueño de un
  producto nuevo.
- Cambié las firmas de `create`, `update`, `partialUpdate` y `delete`
  en `ProductService`/`ProductServiceImpl` para recibir
  `UserDetailsImpl currentUser`, inyectado en el controlador con
  `@AuthenticationPrincipal`.
- En `create()`, el owner ahora sale de
  `findCurrentUserEntity(currentUser)`, que reconsulta al usuario en
  base para asegurarme de que sigue existiendo y no está eliminado
  lógicamente.
- En `update()`, `partialUpdate()` y `delete()` agregué
  `validateOwnership(entity, currentUser)` justo después de buscar el
  producto: si el usuario tiene `ROLE_ADMIN` puede seguir sin
  restricciones; si no, comparo `product.getOwner().getId()` contra
  `currentUser.getId()` y lanzo `AccessDeniedException` si no coinciden.
- Ajusté el handler de `AccessDeniedException` en
  `GlobalExceptionHandler` para que use `ex.getMessage()` en vez de un
  texto fijo, así el cliente recibe el motivo real
  ("No puedes modificar productos ajenos", "Usuario no autenticado",
  etc.) y no un mensaje genérico.

## Evidencias

> Mismo caso: no pude ejecutar el servidor real en este sandbox, así
> que las respuestas siguientes salen directo de la lógica que quedó en
> `ProductServiceImpl.validateOwnership()` y del `GlobalExceptionHandler`
> actualizado.

### Captura de creación de producto con usuario autenticado

```txt
POST /api/products
Authorization: Bearer <token-usuario-A>
```

```json
{
  "name": "Laptop Usuario A",
  "price": 900.0,
  "stock": 10,
  "categoryIds": [1]
}
```

```json
{
  "id": 14,
  "name": "Laptop Usuario A",
  "price": 900.0,
  "stock": 10,
  "owner": { "id": 6, "name": "Usuario A", "email": "usera@ups.edu.ec" },
  "categories": [
    { "id": 1, "name": "Computadoras", "description": "Equipos de cómputo" }
  ],
  "createdAt": "2026-07-10T09:30:02.1112223",
  "updatedAt": null
}
```

Status esperado: `201 Created`. El campo `owner.id` coincide con el id
del usuario del token, aunque el body nunca envió `userId`.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de bloqueo por producto ajeno

```txt
PUT /api/products/14
Authorization: Bearer <token-usuario-B>
```

```json
{
  "timestamp": "2026-07-10T09:31:47.4455661",
  "status": 403,
  "error": "Forbidden",
  "message": "No puedes modificar productos ajenos",
  "path": "/api/products/14"
}
```

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de eliminación de producto ajeno bloqueada

```txt
DELETE /api/products/14
Authorization: Bearer <token-usuario-B>
```

Status esperado: `403 Forbidden`, mismo formato de `ErrorResponse` que
el bloqueo de `PUT`.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

### Captura de ADMIN modificando producto ajeno

```txt
PUT /api/products/14
Authorization: Bearer <token-usuario-B-ahora-ADMIN>
```

Status esperado: `200 OK`. `validateOwnership()` detecta `ROLE_ADMIN` y
deja pasar la modificación aunque Usuario B no sea el dueño.

_(Pendiente: reemplazar con tu propia captura real desde Bruno.)_

## Colección Bruno

Armé una carpeta completa `bruno/Practica13` con los 10 pasos en orden
(crear categoría base, registrar Usuario A y B, crear producto,
actualizar propio, actualizar/eliminar ajeno con `403`, subir a Usuario
B a `ROLE_ADMIN` y repetir actualizar/eliminar con `200`/`204`), con los
tokens guardándose solos vía `script:post-response` en cada login.

## Explicación breve

**¿Qué es ownership?**

Es la relación entre un recurso y el usuario dueño de ese recurso — en
este caso, el campo `owner` de `ProductEntity`. Validar ownership
significa comprobar, antes de dejar modificar o borrar algo, que quien
hace la petición sea efectivamente el dueño del registro (o tenga un
permiso especial, como `ROLE_ADMIN`, que le permita saltarse esa regla).
No es lo mismo que autenticación (¿quién eres?) ni que autorización por
rol (¿qué tipo de acciones puedes hacer en general?): ownership es *¿es
tuyo este recurso en particular?*, y por eso solo se puede resolver
después de buscar el recurso concreto en base de datos.

**¿Por qué no es seguro recibir `userId` en `CreateProductDto`?**

Porque si el cliente puede mandar cualquier `userId` en el body, un
usuario autenticado con id `2` podría enviar `"userId": 5` y crear
productos a nombre del usuario `5`, sin que nada se lo impida — el
servidor confiaría ciegamente en un dato que viene del cliente y que es
trivial de falsificar. La única fuente confiable de "quién soy" es el
token JWT que ya fue validado por `JwtAuthenticationFilter`, así que el
owner tiene que salir de ahí (`@AuthenticationPrincipal`), nunca del
body de la petición.

**¿Cuál es la diferencia entre autorización por rol y autorización por
ownership?**

La autorización por rol (Práctica 12, `@PreAuthorize`) se evalúa
*antes* de ejecutar el método, y solo necesita saber el rol del usuario
autenticado — no le importa qué recurso específico se va a tocar. La
autorización por ownership se evalúa *dentro* del servicio, después de
haber buscado el recurso en base de datos, porque necesita comparar un
dato concreto (`product.getOwner().getId()`) contra el usuario actual.
Por eso una vive en `SecurityConfig`/anotaciones y la otra vive como
lógica de negocio en `ProductServiceImpl`: no hay forma de saber si un
producto es "ajeno" sin haberlo consultado primero.

------------------------------------------------------------------------

# Conclusión

Se desarrolló una API REST completa utilizando Spring Boot aplicando
buenas prácticas de arquitectura backend: separación por capas,
persistencia real en PostgreSQL vía Docker, validación de datos con
DTOs, manejo centralizado de errores, relaciones JPA, filtros
dinámicos, paginación con `Page`/`Slice`, autenticación *stateless* con
JWT, autorización por rol con `@PreAuthorize` y, finalmente, validación
de ownership a nivel de servicio para que cada usuario solo pueda
modificar sus propios recursos (salvo los administradores).

Quedan pendientes, para cuando corra el proyecto con el servidor
levantado de verdad: reemplazar todos los bloques marcados como
"(Pendiente: reemplazar con tu propia captura real desde Bruno)" por
capturas reales desde Bruno, e implementar el endpoint `GET /api/users/me`
que pide la guía de la Práctica 12 (`CurrentUserController`), que
todavía no existe en el código.
