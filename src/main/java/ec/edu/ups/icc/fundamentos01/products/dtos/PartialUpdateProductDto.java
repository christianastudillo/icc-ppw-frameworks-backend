package ec.edu.ups.icc.fundamentos01.products.dtos;


import jakarta.validation.constraints.*;



public class PartialUpdateProductDto {


    @Size(min=3,max=150)
    private String name;



    @PositiveOrZero
    private Double price;



    @PositiveOrZero
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