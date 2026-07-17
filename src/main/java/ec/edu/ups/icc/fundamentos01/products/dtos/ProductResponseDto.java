package ec.edu.ups.icc.fundamentos01.products.dtos;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

import java.time.LocalDateTime;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un producto devueltos por la API")
public class ProductResponseDto {

    @Schema(description = "Identificador único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Lenovo ThinkPad")
    private String name;

    @Schema(description = "Precio del producto", example = "899.99")
    private Double price;

    @Schema(description = "Cantidad disponible en inventario", example = "10")
    private Integer stock;

    @Schema(description = "Usuario propietario del producto")
    private UserResponseDto owner;

    @Schema(description = "Categorías asociadas al producto")
    private Set<CategoryResponseDto> categories;

    @Schema(description = "Fecha y hora de creación del producto")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora de la última actualización del producto")
    private LocalDateTime updatedAt;

    public ProductResponseDto() {
    }

    public ProductResponseDto(Long id, String name, Double price, Integer stock,
            UserResponseDto owner, Set<CategoryResponseDto> categories,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.owner = owner;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public UserResponseDto getOwner() {
        return owner;
    }

    public void setOwner(UserResponseDto owner) {
        this.owner = owner;
    }

    public Set<CategoryResponseDto> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryResponseDto> categories) {
        this.categories = categories;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
