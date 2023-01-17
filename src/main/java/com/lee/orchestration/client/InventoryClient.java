package com.lee.orchestration.client;


import com.lee.orchestration.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class InventoryClient {
    private final WebClient client;

    private static final String DEDUCT = "deduct";
    private static final String RESTORE = "restore";


    public InventoryClient(@Value("${sec03.inventory.service}") String baseUrl) {

        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<InventoryResponse> deduct(InventoryRequest request){
        return this.callInventoryService(DEDUCT,request);
    }

    public Mono<InventoryResponse> restore(InventoryRequest request){
        return this.callInventoryService(RESTORE,request);
    }

    private InventoryResponse buildErrorResponse(InventoryRequest request){
        return InventoryResponse.create(
                request.getProductId(),
                request.getQuantity(),
                null,
                Status.FAILED
        );
    }

    private Mono<InventoryResponse> callInventoryService(String endPoint,InventoryRequest request){
        return this.client
                .post()
                .uri(endPoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .onErrorReturn(this.buildErrorResponse(request))
                ;
    }
}
