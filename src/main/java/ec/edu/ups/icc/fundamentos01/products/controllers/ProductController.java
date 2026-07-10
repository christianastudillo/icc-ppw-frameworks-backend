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
import jakarta.validation.Valid;

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
    @GetMapping("/slice")
    public Slice<ProductResponseDto> findAllSlice(@Valid @ModelAttribute PaginationDto pagination) {
        return service.findAllSlice(pagination);
    }

    /*
     * GET /products/{id}
     */
    @GetMapping("/{id}")
    public ProductResponseDto findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ProductResponseDto create(
            @Valid @RequestBody CreateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.create(dto, currentUser);
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.update(id, dto, currentUser);
    }

    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(
            @PathVariable Long id,
            @Valid @RequestBody PartialUpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        return service.partialUpdate(id, dto, currentUser);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        service.delete(id, currentUser);
    }




    /*
     * GET /products/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<ProductResponseDto> findByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId);
    }

    /*
     * GET /products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDto> findByCategoryId(@PathVariable Long categoryId) {
        return service.findByCategoryId(categoryId);
    }
}
