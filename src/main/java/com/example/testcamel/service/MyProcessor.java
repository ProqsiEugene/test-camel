package com.example.testcamel.service;

import com.example.testcamel.dto.TrainDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class MyProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        TrainDTO trainDTO = exchange.getIn().getBody(TrainDTO.class);
        ServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
        trainDTO.setDate(trainDTO.getDate());

    //    Протестировано, не работают, возвращают null
    //    trainDTO.setIp(exchange.getIn().getHeader("CamelHttpRemoteAddress", String.class));
    //    trainDTO.setIp(exchange.getIn().getHeader("X-Forwarded-For", String.class));

    //  Возвращает 0:0:0:0:0:0:0:1
        trainDTO.setIp(request.getRemoteAddr());

        trainDTO.setTime(LocalTime.now().toString());
        trainDTO.setGuid(UUID.randomUUID().toString());
        exchange.getIn().setBody(trainDTO);
    }
}
