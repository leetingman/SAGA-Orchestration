package com.lee.orchestration.service;

import com.lee.orchestration.client.UserClient;
import com.lee.orchestration.dto.OrchestrationRequestContext;
import com.lee.orchestration.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;
@Service
public class PaymentOrchestrator extends Orchestrator{

    private final UserClient client;

    @Autowired
    public PaymentOrchestrator(UserClient client) {
        this.client = client;
    }

    @Override
    public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
        return this.client.deduct(ctx.getPaymentRequest())
                .doOnNext(ctx::setPaymentResponse)
                .thenReturn(ctx)
                ;
    }

    @Override
    public Predicate<OrchestrationRequestContext> isSuccess() {
        return ctx -> Status.SUCCESS.equals(ctx.getPaymentResponse().getStatus());
    }

    @Override
    public Consumer<OrchestrationRequestContext> cancel() {
        return ctx -> Mono.just(ctx)
                .filter(isSuccess())
                .map(OrchestrationRequestContext::getPaymentRequest)
                .flatMap(this.client::refund)
                .subscribe()
                ;
    }
    //you dont have to go and immediately cancel, Just going to cancel only
    // if it was successful
}
