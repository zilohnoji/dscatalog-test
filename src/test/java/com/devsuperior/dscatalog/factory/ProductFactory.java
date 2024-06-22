package com.devsuperior.dscatalog.factory;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;

import java.time.Instant;


public class ProductFactory {

    public static Product insert(Product entity, ProductRepository repository) {
        return repository.save(entity);
    }

    public static Product createEmptyIdentifierProduct() {
        return new Product(null, "Produto Nome", "produto descrição", 20.00, "imagem url", Instant.now());
    }

    public static Product createProduct(Long id) {
        return new Product(id, "Produto Nome", "produto descrição", 20.00, "imagem url", Instant.now());
    }
}