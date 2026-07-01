package ec.edu.ups.icc.fundamentos01.products.models;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.categories.dtos.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;

public class ProductModel {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Long ownerId;
    private Long categoryId;
    private UserResponseDto owner;
    private CategoryResponseDto category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    public ProductModel() {
    }

    public ProductModel(Long id, String name, Double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /*
     * Construye un ProductModel desde un CreateProductDto.
     *
     * Se usa al crear un nuevo producto desde la API.
     */
    public static ProductModel fromDto(CreateProductDto dto) {
        ProductModel model = new ProductModel();
        model.setName(dto.getName());
        model.setPrice(dto.getPrice());
        model.setStock(dto.getStock());
        model.setOwnerId(dto.getUserId());
        model.setCategoryId(dto.getCategoryId());
        return model;
    }

    /*
     * Construye un ProductModel desde una ProductEntity.
     *
     * Se usa cuando el repositorio devuelve datos desde PostgreSQL.
     */
    public static ProductModel fromEntity(ProductEntity entity) {
        ProductModel model = new ProductModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setPrice(entity.getPrice());
        model.setStock(entity.getStock());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());

        model.setOwnerId(entity.getOwner().getId());
        model.setOwner(new UserResponseDto(
                entity.getOwner().getId(),
                entity.getOwner().getName(),
                entity.getOwner().getEmail()));

        model.setCategoryId(entity.getCategory().getId());
        model.setCategory(new CategoryResponseDto(
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getDescription()));

        return model;
    }

    /*
     * Convierte el ProductModel a ProductEntity para persistir en PostgreSQL.
     *
     * No asigna owner ni category: el servicio los obtiene desde sus
     * repositorios (tras validar que existan) y los asigna sobre la entidad.
     */
    public ProductEntity toEntity() {
        ProductEntity entity = new ProductEntity();
        entity.setName(this.name);
        entity.setPrice(this.price);
        entity.setStock(this.stock);
        return entity;
    }

    /*
     * Convierte el ProductModel a ProductResponseDto para la respuesta al cliente.
     */
    public ProductResponseDto toResponseDto() {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(this.id);
        response.setName(this.name);
        response.setPrice(this.price);
        response.setStock(this.stock);
        response.setOwner(this.owner);
        response.setCategory(this.category);
        response.setCreatedAt(this.createdAt);
        response.setUpdatedAt(this.updatedAt);
        return response;
    }

    /*
     * Actualiza completamente el modelo con los datos del UpdateProductDto (PUT).
     */
    public void update(UpdateProductDto dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.stock = dto.getStock();
        this.categoryId = dto.getCategoryId();
    }

    /*
     * Actualiza parcialmente el modelo con los campos no nulos del DTO (PATCH).
     */
    public void partialUpdate(PartialUpdateProductDto dto) {
        if (dto.getName() != null) {
            this.name = dto.getName();
        }
        if (dto.getPrice() != null) {
            this.price = dto.getPrice();
        }
        if (dto.getStock() != null) {
            this.stock = dto.getStock();
        }
        if (dto.getCategoryId() != null) {
            this.categoryId = dto.getCategoryId();
        }
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public UserResponseDto getOwner() {
        return owner;
    }

    public void setOwner(UserResponseDto owner) {
        this.owner = owner;
    }

    public CategoryResponseDto getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDto category) {
        this.category = category;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
