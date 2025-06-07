package com.example.coupangapiserver.product;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.example.coupangapiserver.product.domain.Product;
import com.example.coupangapiserver.product.domain.ProductDocument;
import com.example.coupangapiserver.product.dto.CreateProductRequestDto;

import java.util.ArrayList;
import java.util.List;

import com.example.coupangapiserver.repository.ProductDocumentRepository;
import com.example.coupangapiserver.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
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

  public List<ProductDocument> searchProducts(String query, String category, double minPrice, double maxPrice, int page, int size) {

    // multi_match 쿼리
    Query multiMatchQuery = MultiMatchQuery.of(m -> m
            .query(query)
            .fields("name^3", "description^1", "category^2")
            .fuzziness("AUTO")
    )._toQuery();

    // term filter 쿼리 : 카테고리가 정확히 일치하는 것만 필터링
    List<Query> filters = new ArrayList<>();
    if (category != null && !category.isEmpty()) {
      Query categoryFilter = TermQuery.of(t -> t
              .field("category.raw")
              .value(category)
      )._toQuery();
      filters.add(categoryFilter);
    }

    // range filter : 가격 범위 필터
    Query priceRangeFilter = NumberRangeQuery.of(r -> r
            .field("price")
            .gte(minPrice)
            .lte(maxPrice)
    )._toRangeQuery()._toQuery();
    filters.add(priceRangeFilter);

    // should : rating > 4.0
    Query ratingShould = NumberRangeQuery.of(r -> r
            .field("rating")
            .gt(4.0)
    )._toRangeQuery()._toQuery();

    // bool query 조합
    Query boolQuery = BoolQuery.of(b -> b
            .must(multiMatchQuery)
            .filter(filters)
            .should(ratingShould)
    )._toQuery();

    // Highlight Query 생성
    HighlightParameters highlightParameters = HighlightParameters.builder()
            .withPreTags("<b>")
            .withPostTags("</b>")
            .build();
    Highlight highlight = new Highlight(highlightParameters, List.of(new HighlightField("name")));
    HighlightQuery highlightQuery = new HighlightQuery(highlight, ProductDocument.class);

    // NativeQuery 생성
    NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .withHighlightQuery(highlightQuery)
            .withPageable(PageRequest.of(page -1, size))
            .build();

    // 쿼리 실행
    SearchHits<ProductDocument> searchHits = this.elasticsearchOperations.search(nativeQuery, ProductDocument.class);
    return searchHits.getSearchHits().stream()
            .map(hit -> {
              ProductDocument productDocument = hit.getContent();
              String highlightedName = hit.getHighlightFields().get("name").get(0);
              productDocument.setName(highlightedName);
              return productDocument;
            })
            .toList();
  }
}
