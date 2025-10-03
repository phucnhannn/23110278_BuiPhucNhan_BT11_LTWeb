package vn.iotstar.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    List<Product> findByBrand(String brand);

    List<Product> findByMadein(String madein);

    List<Product> findByPriceBetween(float minPrice, float maxPrice);

    Boolean existsByName(String name);
}