package ec.edu.ups.icc.fundamentos01.products.models;



public class ProductModel {


    private Long id;


    private String name;


    private Double price;


    private Integer stock;




    public ProductModel(
            Long id,
            String name,
            Double price,
            Integer stock
    ){

        this.id=id;
        this.name=name;
        this.price=price;
        this.stock=stock;

    }



    public Long getId(){

        return id;

    }



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