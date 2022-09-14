package com.lee.orchestration.util;

import com.lee.orchestration.dto.InventoryRequest;
import com.lee.orchestration.dto.OrchestrationRequestContext;
import com.lee.orchestration.dto.PaymentRequest;
import com.lee.orchestration.dto.ShippingRequest;

public class OrchestrationUtil {
    public static void buildRequestContext(OrchestrationRequestContext ctx){
        buildPaymentRequest(ctx);
        buildInventoryRequest(ctx);
        buildShippingRequest(ctx);
    }
    private static void buildPaymentRequest(OrchestrationRequestContext ctx){
        PaymentRequest paymentRequest= PaymentRequest.create(
                ctx.getOrderRequest().getUserId(),
                ctx.getProductPrice()*ctx.getOrderRequest().getQuantity(),
                ctx.getOrderId()
        );
        ctx.setPaymentRequest(paymentRequest);
    }

    public  static void buildInventoryRequest(OrchestrationRequestContext ctx){
        InventoryRequest inventoryRequest = InventoryRequest.create(
                ctx.getOrderId(),
                ctx.getOrderRequest().getProductId(),
                ctx.getOrderRequest().getQuantity()

        );
        ctx.setInventoryRequest(inventoryRequest);
    }
    public static void buildShippingRequest(OrchestrationRequestContext ctx){
        ShippingRequest shippingRequest = ShippingRequest.create(
                ctx.getOrderRequest().getQuantity(),
                ctx.getOrderRequest().getUserId(),
                ctx.getOrderId()
        );
        ctx.setShippingRequest(shippingRequest);
    }


}
