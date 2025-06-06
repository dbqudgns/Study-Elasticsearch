package com.example.coupangapiserver.repository;

import com.example.coupangapiserver.product.domain.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {
}
