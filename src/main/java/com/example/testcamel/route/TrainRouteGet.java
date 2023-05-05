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

          //     .to("jpa:com.example.testcamel.Train.class?query=SELECT * FROM trains t WHERE t.dt_start > CAST('{body.getDate()}' AS TIMESTAMP) ORDER BY dt_start")
          //     .to("jpa:com.example.testcamel.Train.class?query=SELECT * FROM trains t WHERE t.dt_start > (select s.date_session FROM sessions s where s.guid_session = '${body}') ORDER BY t.dt_start")
          //     .to("jpa:com.example.testcamel.Train?query=SELECT t.id_train, t.train_name, t.id_station_start, t.dt_start FROM trains t WHERE t.dt_start > (select s.date_session FROM sessions s where s.guid_session = :#${body}) ORDER BY t.dt_start")

                .end()
                .split().body().threads(5)
                .log("Результат: ${body}")
                .to("log:output");
    }
}
//                .to("sql:SELECT id_train, upper(train_name) AS train_name, id_station_start, dt_start" +
//                        " from trains WHERE dt_start > (select date_session FROM sessions where guid_session = :#${body})" +
//                        " ORDER BY dt_start")


 //.to("jpa:com.example.TrainRoute?query=SELECT t.id_train, t.train_name, t.id_station_start, t.dt_start FROM trains t WHERE t.dt_start > (select s.date_session FROM sessions s where s.guid_session = :#${body}) ORDER BY t.dt_start")