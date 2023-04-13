package sia5;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.channel.DirectChannel;

import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class RouterConfig {

    /**
     * 자바기반 라우터 구성
     *
     * 채널을 통해서 플로우의 컴포넌트 간 데이터를 전달받고 짝수 홀수에 따라 채널을 라우팅 했다.
     */
    @Bean
    @Router(inputChannel = "numberChannel") // 채널로 통합 플로우의 컴포넌트 간 데이터 전달
    public AbstractMessageRouter evenOddRouter(){
        return new AbstractMessageRouter() {
            @Override
            protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {
                Integer number = (Integer) message.getPayload(); // 페이로드 꺼내기
                if (number % 2 == 0){
                    return Collections.singleton(evenChannel()); // 싱글톤 보장 빈
                }
                return Collections.singleton(oddChannel());
            }
        };
    }

    @Bean
    public MessageChannel evenChannel(){
        return new DirectChannel();
    }

    @Bean MessageChannel oddChannel(){
        return new DirectChannel();
    }


    /**
     * DSL 예시
     *
     *  게이트웨이 -> 채널에서 통합 플로우 컴포넌트의 라우터로 이동
     *  라우터 메서드로 메시지의 페이로드 값을 사용할 수 있다.
     *  true false 에 따라서 파일 변환기 실행
     * */

    /*
    @Bean
    public IntegrationFlow testFlow(AtomicInteger integerSource){
        return IntegrationFlows
                .from(MessageChannels.direct("initialCh")) // 채널로 통합 플로우의 컴포넌트간 데이터 전달
        .<Integer, String> route(n -> n%2 == 0 ? "EVEN":"ODD",
                mapping -> mapping.subFlowMapping("EVEN",
                                  sf -> sf.<Integer, Integer> transform(n -> n * 10).handle((i,h) -> {...}))
                                .subFlowMapping("ODD",
                                  sf -> sf.transform(n -> n*10).handle((i,h) -> {...}))).get();

 }
     */


    @Bean
    @InboundChannelAdapter(channel = "file_channel",
    poller=@Poller(fixedDelay = "1000"))
    public MessageSource<File> fileMessageSource(){
        FileReadingMessageSource sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File("input"));
        sourceReader.setFilter(new SimplePatternFileListFilter("file_path"));
        return sourceReader;
    }
}
