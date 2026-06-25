package ec.edu.ups.icc.fundamentos01.products.dtos;


import jakarta.validation.constraints.*;



public class CreateProductDto {



    @NotBlank(message="El nombre es obligatorio")
    @Size(min=3,max=150)
    private String name;



    @NotNull(message="El precio es obligatorio")
    @PositiveOrZero(message="El precio no puede ser negativo")
    private Double price;



    @NotNull(message="El stock es obligatorio")
    @PositiveOrZero(message="El stock no puede ser negativo")
    private Integer stock;



    public String getName(){

        return name;

    }



    public Double getPrice(){

        return price;

    }



    public Integer getStock(){

        return stock;

    }


}