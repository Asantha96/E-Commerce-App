package com.asa.productservice.service;

import com.asa.productservice.dto.ProductRequest;
import com.asa.productservice.dto.ProductResponse;
import com.asa.productservice.model.Product;
import com.asa.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor //same as the constructor of productRepository
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    /*public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }*/

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product {} is saved!", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

       return products.stream().map(product -> mapToProductResponse(product)).toList();

    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
