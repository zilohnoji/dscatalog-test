package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Long fistExistingId;
    private Long dependentId;
    private Long nonExistingId;
    private PageImpl<Product> page;
    private Product entity;

    @BeforeEach
    void setup() {
        fistExistingId = 1L;
        dependentId = 2L;
        nonExistingId = 3L;
        entity = ProductFactory.createProduct(1L);
        page = new PageImpl<>(List.of(entity));

        Mockito.doNothing().when(productRepository).deleteById(fistExistingId);
        Mockito.doThrow(DatabaseException.class).when(productRepository).deleteById(dependentId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productRepository).deleteById(nonExistingId);


        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(productRepository.findById(fistExistingId)).thenReturn(Optional.of(entity));
        Mockito.when(productRepository.save(ArgumentMatchers.any(Product.class))).thenReturn(entity);
        Mockito.when(productRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {

        // Act / Assert
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(fistExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(fistExistingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {

        // Act / Assert
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDependent() {

        // Act / Assert
        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPageProductDTO() {
        // Arrange - Instanciar objetos necessários
        Pageable pageable = PageRequest.of(0, 10);

        // Act - Executar ação necessária
        Page<ProductDTO> pageEntity = productService.findAllPaged(pageable);
        ProductDTO firstDto = pageEntity.getContent().getFirst();

        // Assert - Declarar resultado esperado
        Assertions.assertNotNull(pageEntity);
        Assertions.assertEquals(fistExistingId, firstDto.getId());
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() {

        // Act - Executar ação necessária
        ProductDTO dto = productService.findById(fistExistingId);

        // Assert - Declarar o resultado esperado
        Assertions.assertEquals(fistExistingId, dto.getId());
        Mockito.verify(productRepository, Mockito.times(1)).findById(fistExistingId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdNotExists() {

        // Act - Executar ação necessária
        // Assert - Declarar o resultado esperado
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).findById(nonExistingId);
    }
}