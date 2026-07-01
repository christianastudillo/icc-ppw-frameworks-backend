package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /*
     * Retorna todos los productos no eliminados almacenados en PostgreSQL.
     *
     * Filtra los productos con deleted = true para no exponerlos al cliente.
     */
    @Override
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(ProductModel::fromEntity)
                .map(ProductModel::toResponseDto)
                .toList();
    }

    /*
     * Busca un producto por id.
     *
     * Lanza excepción si no existe o si fue eliminado lógicamente.
     */
    @Override
    public ProductResponseDto findOne(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException("Product not found");
        }

        return ProductModel.fromEntity(entity).toResponseDto();
    }

    /*
     * Crea un nuevo producto y lo persiste en PostgreSQL.
     *
     * Usa los factory methods del modelo de dominio en lugar del mapper.
     */
    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        ProductModel model = ProductModel.fromDto(dto);
        ProductEntity entity = model.toEntity();
        ProductEntity savedEntity = productRepository.save(entity);
        return ProductModel.fromEntity(savedEntity).toResponseDto();
    }

    /*
     * Actualiza completamente un producto existente (PUT).
     *
     * No actualiza productos eliminados lógicamente.
     */
    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted product");
        }

        ProductModel model = ProductModel.fromEntity(entity);
        model.update(dto);

        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());

        ProductEntity savedEntity = productRepository.save(entity);
        return ProductModel.fromEntity(savedEntity).toResponseDto();
    }

    /*
     * Actualiza parcialmente un producto existente (PATCH).
     *
     * No actualiza productos eliminados lógicamente.
     */
    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted product");
        }

        ProductModel model = ProductModel.fromEntity(entity);
        model.partialUpdate(dto);

        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());

        ProductEntity savedEntity = productRepository.save(entity);
        return ProductModel.fromEntity(savedEntity).toResponseDto();
    }

    /*
     * Elimina lógicamente un producto marcando deleted = true.
     *
     * No elimina físicamente el registro de la base de datos.
     * Lanza excepción si el producto ya fue eliminado previamente.
     */
    @Override
    public void delete(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException("Product already deleted");
        }

        entity.setDeleted(true);
        productRepository.save(entity);
    }
}
