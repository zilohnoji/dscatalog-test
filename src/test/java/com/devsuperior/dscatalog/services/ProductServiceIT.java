package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setup() {
        existingId = 1L;
        nonExistingId = 0L;
    }

    @Test
    void deleteShouldDeleteProductWhenIdExists() {

        // Arrange - Instanciar objetos necessários
        // Act - Executar ação necessária
        productService.delete(existingId);

        // Assert - Declarar o que deveria acontecer
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(existingId);
        });
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {

        // Assert - Declarar o que deveria acontecer
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }
}
