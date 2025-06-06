package com.example.coupangapiserver.product;

import com.example.coupangapiserver.product.domain.Product;
import com.example.coupangapiserver.product.domain.ProductDocument;
import com.example.coupangapiserver.product.dto.CreateProductRequestDto;
import java.util.List;

import com.example.coupangapiserver.repository.ProductDocumentRepository;
import com.example.coupangapiserver.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDocumentRepository productDocumentRepository;

  // 페이지별 상품 전체 조회
  public List<Product> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return productRepository.findAll(pageable).getContent();
  }

  // 상품 등록
  public Product createProduct(CreateProductRequestDto createProductRequestDto) {

    Product product = Product.builder()
            .name(createProductRequestDto.getName())
            .description(createProductRequestDto.getDescription())
            .price(createProductRequestDto.getPrice())
            .rating(createProductRequestDto.getRating())
            .category(createProductRequestDto.getCategory())
            .build();

    Product savedProduct = productRepository.save(product);

    ProductDocument productDocument = ProductDocument.builder()
            .id(savedProduct.getId().toString())
            .name(savedProduct.getName())
            .description(savedProduct.getDescription())
            .price(savedProduct.getPrice())
            .rating(savedProduct.getRating())
            .category(savedProduct.getCategory())
            .build();

    productDocumentRepository.save(productDocument);

    return savedProduct;

  }

  //상품 삭제
  public void deleteProduct(Long id) {

    productRepository.deleteById(id);
    productDocumentRepository.deleteById(id.toString());

  }
}
