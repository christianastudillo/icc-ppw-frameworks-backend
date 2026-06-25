package ec.edu.ups.icc.fundamentos01.products.services;


import java.util.List;


import org.springframework.stereotype.Service;


import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;

import ec.edu.ups.icc.fundamentos01.products.entity.ProductEntity;

import ec.edu.ups.icc.fundamentos01.products.mapper.ProductMapper;

import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;



@Service
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;



    public ProductServiceImpl(ProductRepository productRepository) {

        this.productRepository = productRepository;

    }





    /*
     * Retorna todos los productos activos.
     *
     * Los productos eliminados lógicamente
     * no deben aparecer en la respuesta.
     */
    @Override
    public List<ProductResponseDto> findAll() {


        return productRepository.findByDeletedFalse()

                .stream()

                .map(ProductMapper::toModelFromEntity)

                .map(ProductMapper::toResponse)

                .toList();


    }







    /*
     * Busca un producto por id.
     *
     * No permite devolver productos eliminados.
     */
    @Override
    public ProductResponseDto findOne(Long id) {



        ProductEntity entity = productRepository.findById(id)

                .orElseThrow(() ->
                        new IllegalStateException("Product not found")
                );



        if(entity.getDeleted()){


            throw new IllegalStateException(
                    "Product deleted"
            );


        }



        ProductModel model =
                ProductMapper.toModelFromEntity(entity);



        return ProductMapper.toResponse(model);


    }








    /*
     * Crea un producto nuevo.
     *
     * DTO
     * ↓
     * Model
     * ↓
     * Entity
     * ↓
     * PostgreSQL
     */
    @Override
    public ProductResponseDto create(CreateProductDto dto) {



        ProductModel model =
                ProductMapper.toModelFromDTO(dto);



        ProductEntity entity =
                ProductMapper.toEntityFromModel(model);



        ProductEntity savedEntity =
                productRepository.save(entity);



        ProductModel savedModel =
                ProductMapper.toModelFromEntity(savedEntity);



        return ProductMapper.toResponse(savedModel);


    }








    /*
     * Actualiza completamente un producto.
     *
     * No permite modificar productos eliminados.
     */
    @Override
    public ProductResponseDto update(
            Long id,
            UpdateProductDto dto
    ) {



        ProductEntity entity =
                productRepository.findById(id)

                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product not found"
                        )
                );





        if(entity.getDeleted()){


            throw new IllegalStateException(
                    "Cannot update deleted product"
            );


        }





        entity.setName(dto.getName());

        entity.setPrice(dto.getPrice());

        entity.setStock(dto.getStock());





        ProductEntity savedEntity =
                productRepository.save(entity);





        ProductModel model =
                ProductMapper.toModelFromEntity(savedEntity);




        return ProductMapper.toResponse(model);



    }









    /*
     * Actualización parcial.
     *
     * Solo modifica los campos enviados.
     */
    @Override
    public ProductResponseDto partialUpdate(
            Long id,
            PartialUpdateProductDto dto
    ) {



        ProductEntity entity =
                productRepository.findById(id)

                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product not found"
                        )
                );





        if(entity.getDeleted()){


            throw new IllegalStateException(
                    "Cannot update deleted product"
            );


        }





        if(dto.getName()!=null){

            entity.setName(
                    dto.getName()
            );

        }





        if(dto.getPrice()!=null){

            entity.setPrice(
                    dto.getPrice()
            );

        }





        if(dto.getStock()!=null){

            entity.setStock(
                    dto.getStock()
            );

        }






        ProductEntity savedEntity =
                productRepository.save(entity);





        ProductModel model =
                ProductMapper.toModelFromEntity(savedEntity);




        return ProductMapper.toResponse(model);



    }









    /*
     * Eliminación lógica.
     *
     * No borra de PostgreSQL.
     *
     * Solo cambia:
     *
     * deleted = true
     *
     */
    @Override
    public void delete(Long id) {



        ProductEntity entity =
                productRepository.findById(id)

                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product not found"
                        )
                );





        if(entity.getDeleted()){


            throw new IllegalStateException(
                    "Product already deleted"
            );


        }





        entity.setDeleted(true);




        productRepository.save(entity);



    }



}