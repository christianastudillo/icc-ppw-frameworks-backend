package ec.edu.ups.icc.fundamentos01.products.mapper;


import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entity.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;



public class ProductMapper {



    /*
     * DTO -> MODEL
     */
    public static ProductModel toModelFromDTO(
            CreateProductDto dto
    ){


        return new ProductModel(

                null,

                dto.getName(),

                dto.getPrice(),

                dto.getStock()

        );

    }





    /*
     * ENTITY -> MODEL
     */
    public static ProductModel toModelFromEntity(
            ProductEntity entity
    ){


        return new ProductModel(

                entity.getId(),

                entity.getName(),

                entity.getPrice(),

                entity.getStock()

        );

    }






    /*
     * MODEL -> ENTITY
     */
    public static ProductEntity toEntityFromModel(
            ProductModel model
    ){


        ProductEntity entity =
                new ProductEntity(

                        model.getName(),

                        model.getPrice(),

                        model.getStock()

                );


        return entity;

    }







    /*
     * MODEL -> RESPONSE DTO
     */
    public static ProductResponseDto toResponse(
            ProductModel model
    ){



        return new ProductResponseDto(

                model.getId(),

                model.getName(),

                model.getPrice(),

                model.getStock()

        );


    }



}