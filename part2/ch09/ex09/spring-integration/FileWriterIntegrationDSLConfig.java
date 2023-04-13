package sia5;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileReadingMessageSource;

import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;

import java.io.File;

@Configuration
public class FileWriterIntegrationDSLConfig {

    @Bean
    public IntegrationFlow fileWriterFlow(){
        return IntegrationFlows
                .from(MessageChannels.direct("textInChannel")) // 인바운드 어댑터 채널
                .<String, String>transform(t -> t.toUpperCase()) // 변환기 선언
                .handle(Files.outboundAdapter(new File("/tmp/sia5/files"))
                        .fileExistsMode(FileExistsMode.APPEND)
                        .appendNewLine(true)).get();
    }


}
