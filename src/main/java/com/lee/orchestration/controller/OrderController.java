package com.lee.orchestration.controller;


import com.lee.orchestration.dto.OrderRequest;
import com.lee.orchestration.dto.OrderResponse;
import com.lee.orchestration.service.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sec03")
public class OrderController {
    private final OrchestratorService service;

    @Autowired
    public OrderController(OrchestratorService service) {
        this.service = service;
    }

    @PostMapping("order")
    public Mono<ResponseEntity<OrderResponse>> placeOrder(@RequestBody Mono<OrderRequest> mono){
        return this.service.placeOrder(mono)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
