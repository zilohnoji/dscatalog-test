package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.factory.ProductFactory;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.devsuperior.dscatalog.utils.TestUtils.objectToJson;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductResource.class)
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Long firstExistingId;
    private Long dependentId;
    private Long nonExistingId;
    private ProductDTO productDtoEmptyIdentifier;
    private ProductDTO productDtoExists;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setup() {
        firstExistingId = 1L;
        dependentId = 2L;
        nonExistingId = 4L;
        productDtoExists = new ProductDTO(ProductFactory.createProduct(firstExistingId));
        productDtoEmptyIdentifier = new ProductDTO(ProductFactory.createEmptyIdentifierProduct());
        page = new PageImpl<>(List.of(productDtoExists));

        Mockito.when(productService.insert(ArgumentMatchers.any(ProductDTO.class))).thenReturn(productDtoExists);

        Mockito.when(productService.update(eq(firstExistingId), ArgumentMatchers.any(ProductDTO.class))).thenReturn(productDtoExists);
        Mockito.when(productService.update(eq(nonExistingId), ArgumentMatchers.any(ProductDTO.class))).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(productService).delete(firstExistingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);

        Mockito.when(productService.findById(firstExistingId)).thenReturn(productDtoExists);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(productService.findAllPaged(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
    }

    @Test
    void insertShouldReturnProductDTO() throws Exception {
        mockMvc.perform(request(HttpMethod.POST, "/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(productDtoEmptyIdentifier)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void deleteShouldDeleteProductAndReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(request(HttpMethod.DELETE, "/products/{id}", firstExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdNotExists() throws Exception {
        mockMvc.perform(request(HttpMethod.DELETE, "/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(request(HttpMethod.PUT, "/products/{id}", firstExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(productDtoExists)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdNotExists() throws Exception {
        mockMvc.perform(request(HttpMethod.PUT, "/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(ProductFactory.createEmptyIdentifierProduct())))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllShouldReturnPageProductDTO() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/products/{id}", firstExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdNotExists() throws Exception {
        mockMvc.perform(request(HttpMethod.GET, "/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}