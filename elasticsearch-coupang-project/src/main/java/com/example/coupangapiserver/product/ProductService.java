package com.example.coupangapiserver.product;

import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.example.coupangapiserver.product.domain.Product;
import com.example.coupangapiserver.product.domain.ProductDocument;
import com.example.coupangapiserver.product.dto.CreateProductRequestDto;
import java.util.List;

import com.example.coupangapiserver.repository.ProductDocumentRepository;
import com.example.coupangapiserver.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductDocumentRepository productDocumentRepository;
  private final ElasticsearchOperations elasticsearchOperations;

  // 페이지별 상품 전체 조회
  public List<Product> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    return productRepository.findAll(pageable).getContent();
  }

  // 자동 완성
  public List<String> getSuggestions(String query) {
    Query multiMatchQuery = MultiMatchQuery.of(m -> m
            .query(query)
            .type(TextQueryType.BoolPrefix)
            .fields("name.auto_complete", "name.auto_complete._2gram", "name.auto_complete._3gram")
    )._toQuery();

    NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(multiMatchQuery)
            .withPageable(PageRequest.of(0, 5)) //pageNumber : from , pageSize : size
            .build();

    SearchHits<ProductDocument> searchHits = this.elasticsearchOperations.search(nativeQuery, ProductDocument.class);

    return searchHits.stream()
            .map(hit -> {
              ProductDocument productDocument = hit.getContent();
              return productDocument.getName();
            })
            .toList();
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

  // 상품 삭제
  public void deleteProduct(Long id) {

    productRepository.deleteById(id);
    productDocumentRepository.deleteById(id.toString());

  }
}
