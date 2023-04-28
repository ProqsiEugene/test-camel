package com.example.testcamel.service;

import com.example.testcamel.model.TrainDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;
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
        trainDTO.setIp(request.getRemoteAddr());
        trainDTO.setTime(LocalTime.now().toString());
        trainDTO.setGuid(UUID.randomUUID().toString());
        exchange.getIn().setBody(trainDTO);
    }
}
