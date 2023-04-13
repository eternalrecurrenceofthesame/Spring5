package sia5.splitter;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.Splitter;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.router.MessageRouter;
import org.springframework.integration.router.PayloadTypeRouter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static sia5.splitter.PurchaseOrder.*;

//@Configuration
public class OrderSplitterConfig {


    /**
     * 자바 설정으로 분배기 만들기
     *
     * 주문을 주문 대금 청구와 주문 항목 두 가지 로 나눔 Splitter 클래스
     */
    @Bean
    @Splitter(inputChannel = "poChannel",
              outputChannel = "splitOrderChannel")
    public OrderSplitter orderSplitter(){
        return new OrderSplitter();
    }


    /**
     * 라우터를 사용해서 분할 메시지 처리하기
     *
     * PayloadTypeRouter 는 각 페이로드 타입을 기반으로 서로 다른 채널에 메시지를 전달한다.
     */
     @Bean
     @Router(inputChannel = "splitOrderChannel")
    public MessageRouter splitOrderRouter(){
         PayloadTypeRouter router = new PayloadTypeRouter();
         router.setChannelMapping(
                 BillingInfo.class.getName(), "billingInfoChannel");
         router.setChannelMapping(
                 List.class.getName(), "lineItemsChannel");

         return router;
     }

    @Splitter(inputChannel = "lineItemsChannel", outputChannel="lineItemChannel") // s 차이 조심 ㅎㅎ
    public List<LineItem> lineItemSplitter(List<LineItem> lineItems){

        for (LineItem lineItem : lineItems) {
            // 별도로 라인 아이템들을 처리하는 메서드를 작성하고 채널을 통해서 플로우로 전달한다
        }
        return lineItems;
    }

    @Bean
    public IntegrationFlow fileReaderFlow(){
         return IntegrationFlows
                 .from(Files.inboundAdapter(new File("input-dir"))
                         .patternFilter("file_pattern")).get();
    }


}
