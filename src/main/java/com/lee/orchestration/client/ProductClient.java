package com.lee.orchestration.client;

import com.lee.orchestration.dto.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductClient {

    private final WebClient client;

    public ProductClient(@Value("${orchestration.product.service}" ) String baseUrl){
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    public Mono<Product> getProduct(Integer id) {
        return this.client
                .get()
                .uri("{id}",id)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorResume(ex -> Mono.empty());

    }
    //retrieve -> get response body Decoder


}
