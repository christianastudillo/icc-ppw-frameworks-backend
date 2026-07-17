package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;

import ec.edu.ups.icc.fundamentos01.core.dtos.PaginationDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Productos", description = "Endpoints para gestión de productos")
@SecurityRequirement(name = "bearerAuth") // Requiere autenticación JWT para todos los endpoints de este controlador@RestController
@RestController
@RequestMapping("/products")


public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /*
     * GET /products
     */
    /*
     * GET /products
     */
    @Operation(summary = "Listar productos", description = "Devuelve la lista completa de productos. Requiere rol ADMIN.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @ApiResponse(responseCode = "403", description = "No tiene permisos para acceder a este recurso.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }


    /*
     * Endpoint paginado usando Page.
     *
     * GET /products/page
     * GET /products/page?page=0&size=5
     * GET /products/page?page=0&size=5&sortBy=price&direction=desc
     */
    @Operation(summary = "Listar productos paginados", description = "Devuelve una página de productos usando parámetros de paginación (page, size, sortBy, direction).")
    @ApiResponse(responseCode = "200", description = "Página de productos obtenida exitosamente.")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @GetMapping("/page")
    public Page<ProductResponseDto> findAllPage(@Valid @ModelAttribute PaginationDto pagination) {
        return service.findAllPage(pagination);
    }

    /*
     * Endpoint paginado usando Slice.
     *
     * GET /products/slice
     * GET /products/slice?page=0&size=5
     * GET /products/slice?page=0&size=5&sortBy=createdAt&direction=desc
     */
    @Operation(summary = "Listar productos paginados (slice)", description = "Devuelve un slice de productos del usuario autenticado usando parámetros de paginación (page, size, sortBy, direction).")
    @ApiResponse(responseCode = "200", description = "Slice de productos obtenido exitosamente.")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(
            @Valid @ModelAttribute PaginationDto pagination,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.findAllSlice(pagination, currentUser);
    }

    /*
     * GET /products/{id}
     */
    @Operation(summary = "Obtener producto por ID", description = "Devuelve los datos de un producto específico según su identificador.")
    @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente.")
    @ApiResponse(responseCode = "404", description = "No se encontró un producto con el ID indicado.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @Operation(summary = "Crear producto", description = "Crea un nuevo producto asociado al usuario autenticado.")
    @ApiResponse(responseCode = "200", description = "Producto creado exitosamente.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @PostMapping
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.create(dto, currentUser);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza completamente los datos de un producto existente.")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @ApiResponse(responseCode = "403", description = "No tiene permisos para actualizar este producto.")
    @ApiResponse(responseCode = "404", description = "No se encontró un producto con el ID indicado.")
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.update(id, dto, currentUser);
    }

    @Operation(summary = "Actualizar producto parcialmente", description = "Actualiza uno o más campos de un producto existente.")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente.")
    @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de entrada incorrectos.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @ApiResponse(responseCode = "403", description = "No tiene permisos para actualizar este producto.")
    @ApiResponse(responseCode = "404", description = "No se encontró un producto con el ID indicado.")
    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.partialUpdate(id, dto, currentUser);
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto existente según su identificador.")
    @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar este producto.")
    @ApiResponse(responseCode = "404", description = "No se encontró un producto con el ID indicado.")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        service.delete(id, currentUser);
    }




    /*
     * GET /products/user/{userId}
     */
    @Operation(summary = "Listar productos por usuario", description = "Devuelve la lista de productos pertenecientes a un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @ApiResponse(responseCode = "404", description = "No se encontró un usuario con el ID indicado.")
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }

    /*
     * GET /products/category/{categoryId}
     */
    @Operation(summary = "Listar productos por categoría", description = "Devuelve la lista de productos pertenecientes a una categoría específica.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente.")
    @ApiResponse(responseCode = "401", description = "No autenticado.")
    @ApiResponse(responseCode = "404", description = "No se encontró una categoría con el ID indicado.")
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> findByCategoryId(@PathVariable Long categoryId) {
        return service.findByCategoryId(categoryId);
    }
}
