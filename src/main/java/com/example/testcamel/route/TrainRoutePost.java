package com.example.testcamel.route;

import com.example.testcamel.dto.DateDTO;
import com.example.testcamel.dto.TrainDTO;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.UUID;

@Component(value = "trainRoute")
public class TrainRoutePost extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

        rest().post("/train")
                .consumes("application/json")
                .type(DateDTO.class)
                .to("direct:convertToTrain");

        from("direct:convertToTrain")
                .routeId("convertToTrain")
                .log("Что попадает в конвектор1: " + "${body}")
                .to("log:?showBody=true&showHeaders=true") // смотрю все заголовки для определения ip
                .process(exchange -> {
                    TrainDTO trainDTO = TrainDTO.builder()
                            .date(exchange.getIn().getBody(DateDTO.class).getDate().toString())
                            .time(LocalTime.now().toString())
                            .ip(exchange.getIn().getHeader("host", String.class))
                            .guid(UUID.randomUUID().toString())
                            .build();
                    exchange.getIn().setBody(trainDTO);
                })

                .setProperty("guidValue", simple("${body.guid}")) // сохраняю guid в переменную guidValue
                .log("${body}")
                .to("bean:protobufService?method=convertDtoToProtobuf")
                .log("Что выходит из bodyMessage после протобаф: " + "${body}")
                .to("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}")
                .transform().simple("${body}")
                .setBody(simple("${exchangeProperty.guidValue}")); //достаю тело из переменной bodyValue и сохр в body
    }
}
