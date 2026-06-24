package ec.edu.ups.icc.fundamentos01.products.dtos;


/*
 * DTO para recibir los datos al crear un producto.
 *
 * Solo contiene los campos que el cliente debe enviar.
 * - createdAt: el cliente puede enviarlo, pero el backend lo ignora.
 *   En el mapper se asigna con LocalDateTime.now() para evitar manipulación.
 *   Ejemplo en el mapper: model.setCreatedAt(LocalDateTime.now());
 */

public class CreateProductDto {
    private String name;
    private Double price;
    private Integer stock;

    public CreateProductDto() {
    }

    public CreateProductDto(String name, Double price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
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

}
