package ec.edu.ups.icc.fundamentos01.products.entity;


import jakarta.persistence.*;



@Entity
@Table(name="products")
public class ProductEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable=false,length=150)
    private String name;


    @Column(nullable=false)
    private Double price;


    @Column(nullable=false)
    private Integer stock;


    @Column(nullable=false)
    private Boolean deleted=false;



    public ProductEntity(){

    }



    public ProductEntity(
            String name,
            Double price,
            Integer stock
    ){

        this.name=name;
        this.price=price;
        this.stock=stock;
        this.deleted=false;

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


    public Boolean getDeleted(){

        return deleted;

    }



    public void setDeleted(Boolean deleted){

        this.deleted=deleted;

    }
    public void setName(String name){

    this.name=name;

    }


    public void setPrice(Double price){

        this.price=price;

    }


    public void setStock(Integer stock){

        this.stock=stock;

    }



    public void update(String name, Double price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }


}