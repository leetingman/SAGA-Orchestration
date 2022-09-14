package com.lee.orchestration.service;


import com.lee.orchestration.dto.OrchestrationRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class OrderCancellationService {

    private final List<Orchestrator> orchestrator;
    private Sinks.Many<OrchestrationRequestContext> sink;
    private Flux<OrchestrationRequestContext> flux;

    @Autowired
    public OrderCancellationService(List<Orchestrator> orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostConstruct
    public void init(){
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
        this.flux= this.sink.asFlux().publishOn(Schedulers.boundedElastic());
        orchestrator.forEach(o->this.flux.subscribe(o.cancel()));
    }

    //when we get received this stuff, just do give it to the sink
    public void cancelOrder(OrchestrationRequestContext ctx){
        this.sink.tryEmitNext(ctx);
    }

    //summary
    // whatever data put in cancelOrder method , all this flux bubble subscribers
    // then Who is subscribers , that is list of Orchestrator
    // So all subscriber receiving cancel ,each and every body
    // orchestrator emits a somebody gives tha request context to cancel this
    // they will be receiving this and they will be take the appropriate action

}
