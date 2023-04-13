package sia5.splitter;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;


import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import static sia5.splitter.PurchaseOrder.*;

public class OrderSplitterDSLConfig {

    @Bean
    public OrderSplitter orderSplitter(){
    return new OrderSplitter();
    }

    /*
    @Bean
    public IntegrationFlow testFlow(){
        return IntegrationFlows.from(MessageChannels.direct("")) // 인바운드
                .split(orderSplitter()) // 스플리터
                .<Object, String>route( // DSL 사용시 페이로드 P 꺼내줌
                        p -> {
                        if (p.getClass().isAssignableFrom(BillingInfo.class)){
                            return "BILLING_INFO";
                        }else{
                            return "LINE_ITEMS";
                        }}, mapping -> mapping.subFlowMapping("BILLING_INFO",
                                sf -> sf.<BillingInfo> handle((billingInfo, h) -> {...}))
                                .subFlowMapping("LINE_ITEMS",          // lineItems 스플리터
                                        sf -> sf.split().<LineItem> handle((lineItem, h) -> {...})).get());
    }

     */





    }
