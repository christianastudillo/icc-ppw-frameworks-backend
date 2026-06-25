package ec.edu.ups.icc.fundamentos01.products.repositories;



import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;


import ec.edu.ups.icc.fundamentos01.products.entity.ProductEntity;



public interface ProductRepository 
extends JpaRepository<ProductEntity,Long>{



    List<ProductEntity> findByDeletedFalse();



}