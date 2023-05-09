package com.example.testcamel.route;

import com.example.testcamel.model.Train;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component(value = "trainRouteGet")
public class TrainRouteGet extends RouteBuilder {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").bindingMode(RestBindingMode.auto);

        rest().get("/check-guid/{guid}")
                .consumes("application/json")
                .to("direct:checkGuid");

        from("direct:checkGuid")
                .routeId("checkGuid")
                .setBody(header("guid"))
                .log("Полученный GUID из второго реста: ${body}")
                .toD("jpa:com.example.testcamel.model.Session?query=select dateSession from Session o where  o.guidSession = '${body}'")
                .log("После DB: ${body}")
                    .choice()
                        .when(body().regex("^\\s*$"))
                        .setBody(constant("GUID not found"))
                    .otherwise()
                .toD("jpa:com.example.testcamel.model.Train?query=select idTrain, dt_start, idStationStart, trainName from Train where dt_start > '${body}'")
                .split().body().threads(5)
                .process(exchange -> {
                    String json = objectMapper.writeValueAsString(exchange.getIn().getBody());
                    String[] fields = json.split(",");

                    Train train = Train.builder()
                            .idTrain(Long.parseLong(fields[0].replaceAll("\\D+", "")))
                            .dt_start(fields[1])
                            .idStationStart(Long.parseLong(fields[2].replaceAll("\\D+", "")))
                            .trainName(fields[3].replaceAll("\\]", "").toUpperCase())
                            .build();
                    exchange.getIn().setBody(train);
                })
                .to("log:output");
    }
}
// Работает правильно НО не JPA
//                .to("sql:SELECT id_train, upper(train_name) AS train_name, id_station_start, dt_start" +
//                        " from trains WHERE dt_start > (select date_session FROM sessions where guid_session = :#${body})" +
//                        " ORDER BY dt_start")