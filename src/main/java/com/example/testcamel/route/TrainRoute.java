package com.example.testcamel.route;

import com.example.testcamel.model.TrainDTO;
import com.example.testcamel.service.MyProcessor;
import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component(value = "trainRoute")

public class TrainRoute extends RouteBuilder {

    @BeanInject("myProcessor")
    private MyProcessor myProcessor;

    @Override
    public void configure() throws Exception {

        //настройка рест
        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);


        //рест
        rest().post("/train")
                .consumes("application/json")
                .type(TrainDTO.class)
                .to("direct:body");

        from("direct:body")
                .routeId("bodyMessage")
                .process(myProcessor)
                .log(LoggingLevel.INFO, "${body.getIp}")
                .log(LoggingLevel.INFO, "${body.getTime}")
                .log(LoggingLevel.INFO, "${body.getDate}")
                .log(LoggingLevel.INFO, "${body.getGuid}")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        TrainDTO trainDTO = exchange.getIn().getBody(TrainDTO.class);

                        exchange.setProperty("guid", trainDTO.getGuid());
                        exchange.setProperty("ip", trainDTO.getIp());
                        exchange.setProperty("time", trainDTO.getTime());
                        exchange.setProperty("date", trainDTO.getDate());
                    }
                })
                .to("bean:protobufService?method=convertDtoToProtobuf")
                .to("direct:kafka");


        from("direct:kafka")
                .routeId("sendToKafka")
                .to("kafka:{{kafka.topic}}?brokers={{kafka.brokers}}")
                .transform().simple("${body}");
    }
}
