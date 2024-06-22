package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryIT {

    @Autowired
    protected ProductRepository productRepository;

    private Long firstExistingId;
    private Long lastExistingId;

    @BeforeEach
    void setup() {
        firstExistingId = 1L;
        lastExistingId = productRepository.findAll().getLast().getId();
    }

    @Test
    void deleteShouldDeleteProductWhenIdExists() {

        // Act - Executar a ação necessária
        productRepository.deleteById(firstExistingId);

        // Assert - Declarar o que deveria acontecer
        Assertions.assertFalse(productRepository.findById(firstExistingId).isPresent());
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {

        // Arrange - Instanciar objetos necessários
        Product entity = ProductFactory.createEmptyIdentifierProduct();

        // Act - Executar ação necessária
        Product entitySaved = productRepository.save(entity);

        // Assert - Declarar o que deveria acontecer
        Assertions.assertNotNull(entitySaved.getId());
        Assertions.assertEquals((lastExistingId + 1), entitySaved.getId());
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenIdNotExists() {

        // Arrange - Instanciar objetos necessários / Act - Executar ação necessária
        Optional<Product> entity = productRepository.findById(lastExistingId + 1);

        // Assert - Declarar o que deveria acontecer
        Assertions.assertFalse(entity.isPresent());
    }

    @Test
    void findByIdShouldReturnProductWhenIdExists() {

        // Arrange - Instanciar objetos necessários / Act - Executar ação necessária
        Optional<Product> entity = productRepository.findById(firstExistingId);

        // Assert - Declarar o que deveria acontecer
        Assertions.assertTrue(entity.isPresent());
    }
}