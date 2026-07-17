package ec.edu.ups.icc.fundamentos01.products.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(description = "Filtros opcionales para búsqueda de productos por categoría")
public class ProductFilterByCategoryDto {

    @Schema(description = "Nombre o parte del nombre del producto", example = "gaming")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String name;

    @Schema(description = "Precio mínimo del rango de búsqueda", example = "500")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio mínimo no puede ser negativo")
    private Double minPrice;

    @Schema(description = "Precio máximo del rango de búsqueda", example = "1500")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio máximo no puede ser negativo")
    private Double maxPrice;

    @Schema(description = "ID del usuario propietario a filtrar", example = "1")
    @Min(value = 1, message = "El ID de usuario debe ser mayor a 0")
    private Long userId;

    /*
     * Valida que el rango de precios sea coherente.
     *
     * Si ambos valores existen, maxPrice debe ser mayor o igual a minPrice.
     */
    public boolean hasValidPriceRange() {
        if (minPrice != null && maxPrice != null) {
            return maxPrice >= minPrice;
        }

        return true;
    }

    public ProductFilterByCategoryDto() {
    }

    public ProductFilterByCategoryDto(String name, Double minPrice, Double maxPrice, Long userId) {
        this.name = name;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
