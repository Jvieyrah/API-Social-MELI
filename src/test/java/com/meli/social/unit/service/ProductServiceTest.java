package com.meli.social.unit.service;

import com.meli.social.exception.ProductNotFoundException;
import com.meli.social.post.dto.ProductDTO;
import com.meli.social.post.impl.ProductService;
import com.meli.social.post.inter.IProductRepository;
import com.meli.social.post.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService - Get Product Tests")
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar o produto quando existir")
    void shouldReturnProductWhenExists() {
        Product product = new Product(1001, "Mouse Gamer", "Periférico", "Logitech", "Preto", "Teste");
        when(productRepository.findById(1001)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1001);

        assertNotNull(result);
        assertEquals(1001, result.getProductId());
        assertEquals("Mouse Gamer", result.getProductName());
        verify(productRepository, times(1)).findById(1001);
    }

    @Test
    @DisplayName("Deve lançar ProductNotFoundException quando não existir")
    void shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        ProductNotFoundException ex = assertThrows(
                ProductNotFoundException.class,
                () -> productService.getProductById(999)
        );

        assertEquals("Produto não encontrado: 999", ex.getMessage());
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando productId for nulo")
    void shouldThrowWhenProductIdIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> productService.getProductById(null)
        );

        assertEquals("ProductId não pode ser nulo", ex.getMessage());
        verifyNoInteractions(productRepository);
    }
}
