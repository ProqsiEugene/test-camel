package com.example.testcamel.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component(value = "trainRouteFromKafka")
public class TrainRouteFromKafka extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}")
                .routeId("fromKafka")
                .to("bean:protobufService?method=convertProtobufToDto")
                .log("Что читаю из кафки после конвертации: " + "${body}")
                .setBody(simple("${body}"))
                .log("После метода setBody в sendDtoToDB: " + "${body}")
                .setProperty("guidValue2", simple("${body.guid}"))
                .toD("jpa:com.example.testcamel.model.Session?nativeQuery=insert into sessions (time_session, ip_session, " +
                        "date_session, guid_session) values ('${body.getTime}', '${body.getIp}', '${body.getDate}', '${body.getGuid}')")
                .setBody(simple("${exchangeProperty.guidValue2}"))
                .log("guid: " + "${body}")
                .to("log:output");
    }
}
