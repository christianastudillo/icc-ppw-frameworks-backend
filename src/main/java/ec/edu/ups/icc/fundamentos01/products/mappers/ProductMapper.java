package ec.edu.ups.icc.fundamentos01.products.mappers;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

public class ProductMapper {

    /*
     * Convierte un CreateProductDto en ProductModel.
     */
    public static ProductModel toModelFromDTO(CreateProductDto dto) {
        ProductModel model = new ProductModel();
        model.setName(dto.getName());
        model.setPrice(dto.getPrice());
        model.setStock(dto.getStock());
        return model;
    }

    /*
     * Convierte una ProductEntity en ProductModel.
     *
     * Se usa cuando el repositorio devuelve datos desde PostgreSQL.
     */
    public static ProductModel toModelFromEntity(ProductEntity entity) {
        ProductModel model = new ProductModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setPrice(entity.getPrice());
        model.setStock(entity.getStock());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());
        return model;
    }

    /*
     * Convierte un ProductModel en ProductEntity.
     *
     * Se usa antes de guardar datos en la base de datos.
     */
    public static ProductEntity toEntityFromModel(ProductModel model) {
        ProductEntity entity = new ProductEntity();
        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());
        return entity;
    }

    /*
     * Convierte un ProductModel en ProductResponseDto.
     *
     * No expone createdAt ni campos internos.
     */
    public static ProductResponseDto toResponse(ProductModel model) {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(model.getId());
        response.setName(model.getName());
        response.setPrice(model.getPrice());
        response.setStock(model.getStock());
        return response;
    }
}
