package ec.edu.ups.icc.fundamentos01.products.controllers;


import java.util.List;


import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;


import ec.edu.ups.icc.fundamentos01.products.dtos.*;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;



@RestController
@RequestMapping("/products")
public class ProductController {



private final ProductService service;



public ProductController(ProductService service){

this.service=service;

}





@PostMapping
public ProductResponseDto create(
        @Valid @RequestBody CreateProductDto dto
){

return service.create(dto);

}






@GetMapping
public List<ProductResponseDto> findAll(){

return service.findAll();

}





@PutMapping("/{id}")
public ProductResponseDto update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateProductDto dto
){

return service.update(id,dto);

}






@PatchMapping("/{id}")
public ProductResponseDto partialUpdate(
        @PathVariable Long id,
        @Valid @RequestBody PartialUpdateProductDto dto
){

return service.partialUpdate(id,dto);

}





@DeleteMapping("/{id}")
public void delete(
        @PathVariable Long id
){

service.delete(id);

}



}