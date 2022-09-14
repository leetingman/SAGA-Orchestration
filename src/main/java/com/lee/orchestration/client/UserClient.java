package com.lee.orchestration.client;

import com.lee.orchestration.dto.PaymentRequest;
import com.lee.orchestration.dto.PaymentResponse;
import com.lee.orchestration.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserClient {
    private final WebClient client;

    private static final String DEDUCT = "deduct";
    private static final String REFUND = "refund";


    public UserClient(@Value("${orchestration.user.service") String baseUrl) {

        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<PaymentResponse> deduct(PaymentRequest request){
        return this.callUserService(DEDUCT,request);
    }

    public Mono<PaymentResponse> refund(PaymentRequest request){
        return this.callUserService(REFUND,request);
    }

    private PaymentResponse buildErrorResponse(PaymentRequest request){
        return PaymentResponse.create(
                request.getUserId(),
                null,
                request.getAmount(),
                Status.FAILED
        );
    }

    private Mono<PaymentResponse> callUserService(String endPoint,PaymentRequest request){
        return this.client
                .post()
                .uri(endPoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .onErrorReturn(this.buildErrorResponse(request))
                ;
    }
    //If something some failure happens stuff
    // going to use buildErrorResponse to let service more know that a failed something like Status Failed

}
