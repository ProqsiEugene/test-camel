package com.example.testcamel.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class TrainDTO {
    @XmlElement
    private String time;

    @XmlElement
    private String ip;

    @XmlElement
    private String date;

    @XmlElement
    private String guid;
}
