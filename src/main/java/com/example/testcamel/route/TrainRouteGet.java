package com.example.testcamel.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component(value = "trainRouteGet")
public class TrainRouteGet extends RouteBuilder {

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

                .to("sql:SELECT id_train, upper(train_name) AS train_name, id_station_start, dt_start" +
                        " from trains WHERE dt_start > (select date_session FROM sessions where guid_session = :#${body})" +
                        " ORDER BY dt_start")

                //выводит весь список при неправильном GUID разобраться!!! Запросы рабочие

//                .toD("jpa:com.example.testcamel.model.Session?query=select dateSession from Session o where  o.guidSession = '${body}'")
//                .log("После DB: ${body}")
//                .toD("jpa:com.example.testcamel.model.Train?query=select idTrain, dt_start, idStationStart, trainName from Train where dt_start > '${body}'")

                // Работает правильно НО не JPA
                
                .end()
                .split().body().threads(5)
                .log("Результат: ${body}")
                .to("log:output");
    }
}
//                .to("sql:SELECT id_train, upper(train_name) AS train_name, id_station_start, dt_start" +
//                        " from trains WHERE dt_start > (select date_session FROM sessions where guid_session = :#${body})" +
//                        " ORDER BY dt_start")