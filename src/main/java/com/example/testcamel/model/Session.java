package com.example.testcamel.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @Column(name = "id_session")
    private Long idSession;

    @Column(name = "ip_session")
    private String ipSession;

    @Column(name = "guid_session")
    private String guidSession;

    @Column(name = "time_session")
    private String timeSession;

    @Column(name = "date_session")
    private String dateSession;
}
