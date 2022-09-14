package com.lee.orchestration.service;

import com.lee.orchestration.dto.OrchestrationRequestContext;
import com.lee.orchestration.dto.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderFulfillmentService {


    private final List<Orchestrator> orchestrators;

    @Autowired
    public OrderFulfillmentService(List<Orchestrator> orchestrators) {
        this.orchestrators = orchestrators;
    }

    public Mono<OrchestrationRequestContext> placeOrder(OrchestrationRequestContext ctx){
       //getting a publisher , isnt actually invoking the create method
        List<Mono<OrchestrationRequestContext>> list = orchestrators.stream()
                .map(o -> o.create(ctx))
                .collect(Collectors.toList());
        // getta all the list of publishers

        return Mono.zip(list,a->a[0])
                .cast(OrchestrationRequestContext.class)
                .doOnNext(this::updateStatus);
//      they all return same Object that is reason why get just one argument
    }

    private void updateStatus(OrchestrationRequestContext ctx){
//        boolean allSuccess = this.orchestrators.stream().allMatch(o->o.isSuccess().test(ctx));
        var allSuccess = this.orchestrators.stream().allMatch(o->o.isSuccess().test(ctx));
        var status = allSuccess ? Status.SUCCESS:Status.FAILED;
        ctx.setStatus(status);
    }


}
