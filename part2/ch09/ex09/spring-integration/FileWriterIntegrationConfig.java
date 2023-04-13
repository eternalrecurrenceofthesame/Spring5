package sia5;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.GenericMessage;
import tacos.Order;
import tacos.data.OrderRepository;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

//@Configuration
public class FileWriterIntegrationConfig {

    /**
     * 파일 변환기
     *
     * textInChannel 로부터 메시지를 받아서 FileWritingMessageHandler 로 값을 넘겨준다.
     */
    @Bean
    @Transformer(inputChannel = "textInChannel",
                 outputChannel = "fileWriterChannel")
    public GenericTransformer<String, String> upperCaseTransformer(){ //string 을 stirng 으로 바꿈
        return text -> text.toUpperCase(); // 대문자 변환기
    }

    /**
     * 파일-쓰기 메시지 핸들러
     *
     * 메시지 페이로드를 지정된 디렉터리의 파일에 쓴다. 이때 파일 이름은 해당 메시지의
     * FILENAME(file_name) 헤더에 지정된 것을 사용한다.
     */
    @Bean
    @ServiceActivator(inputChannel = "fileWriterChannel") // 파일 쓰기 빈을 선언한다
    public FileWritingMessageHandler fileWriter(){
        FileWritingMessageHandler handler
                = new FileWritingMessageHandler(new File("/temp/sia5/files"));

        handler.setExpectReply(false); // 응답 채널 사용 x
        handler.setFileExistsMode(FileExistsMode.APPEND);
        handler.setAppendNewLine(true);

        return handler;
    }



}
