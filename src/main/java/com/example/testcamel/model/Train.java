package com.example.testcamel.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "trains")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @Column(name = "id_train")
    private Long idTrain;

    @Column(name = "dt_start")
    private String dt_start;

    @Column(name = "id_station_start")
    private Long idStationStart;

    @Column(name = "train_name")
    private String trainName;
}
