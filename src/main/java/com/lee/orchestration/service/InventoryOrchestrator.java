package com.lee.orchestration.service;

import com.lee.orchestration.client.InventoryClient;
import com.lee.orchestration.dto.OrchestrationRequestContext;
import com.lee.orchestration.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
public class InventoryOrchestrator extends  Orchestrator{

    private final InventoryClient client;

    @Autowired
    public InventoryOrchestrator(InventoryClient client) {
        this.client = client;
    }

    @Override
    public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
        return this.client.deduct(ctx.getInventoryRequest())
                .doOnNext(ctx::setInventoryResponse)
                .thenReturn(ctx)
                ;
    }

    @Override
    public Predicate<OrchestrationRequestContext> isSuccess() {
        return ctx-> Status.SUCCESS.equals(ctx.getInventoryResponse().getStatus());
    }

    @Override
    public Consumer<OrchestrationRequestContext> cancel() {
        return ctx-> Mono.just(ctx)
                .filter(isSuccess())
                .map(OrchestrationRequestContext::getInventoryRequest)
                .flatMap(this.client::restore)
                .subscribe()
                ;

    }
}
