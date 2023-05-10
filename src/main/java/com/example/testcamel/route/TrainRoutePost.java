package com.example.testcamel.route;

import com.example.testcamel.dto.DateDTO;
import com.example.testcamel.dto.TrainDTO;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
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
                //    ServletRequest request = exchange.getIn().getBody(HttpServletRequest.class);
                    TrainDTO trainDTO = TrainDTO.builder()
                            .date(exchange.getIn().getBody(DateDTO.class).getDate().toString())
                            .time(LocalTime.now().toString())
                            .ip(exchange.getIn().getHeader("host", String.class))
                            .guid(UUID.randomUUID().toString())
                            .build();
                    exchange.getIn().setBody(trainDTO);
                })
                .setProperty("bodyValue", body()) // сохраняю body в переменную bodyValue
               // .to("log:?showBody=true&showHeaders=true")  //смотрю все заголовки
                .log("Что выходит из конвектора: " + "${body}")
                .to("direct:body");

        from("direct:body")
                .routeId("bodyMessage")
                .log("Что попадает в bodyMessage: " + "${body}")
                .to("bean:protobufService?method=convertDtoToProtobuf")
                .log("Что выходит из bodyMessage после протобаф: " + "${body}")

                .to("direct:kafka");

        from("direct:kafka")
                .routeId("sendToKafka")
                .to("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}")
                .transform().simple("${body}")
                .setBody(simple("${exchangeProperty.bodyValue.getGuid}")); //достаю тело из переменной bodyValue и сохр в body
           //     .to("direct:sendDtoToDB");

        from("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}")
                .routeId("sendDtoToDB")
                .log("Что читаю из кафки: " + "${body}")
                .to("bean:protobufService?method=convertProtobufToDto")
                .log("Что читаю из кафки после конвертации: " + "${body}")


                .setBody(simple("${body}"))
                .log("После метода setBody в sendDtoToDB: " + "${body}")

                .to("log:?showBody=true&showHeaders=true") // смотрю все заголовки для определения ip
                .toD("jpa:com.example.testcamel.model.Session?nativeQuery=insert into sessions (time_session, ip_session, " +
                        "date_session, guid_session) values ('${body.getTime}', '${body.getIp}', '${body.getDate}', '${body.getGuid}')")
                .to("log:output")
                .to("direct:fromDtoToDB");

        from("direct:fromDtoToDB")
                .routeId("fromDtoToDB")
                .log("До  fromDtoToDB: " + "${body}")
                .setBody(simple("${exchangeProperty.bodyValue.getGuid}")) //достаю тело из переменной bodyValue и сохр в body
                .log("guid: " + "${body}")
                .to("log:output");
    }
}