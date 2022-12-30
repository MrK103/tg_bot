package by.mrk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@EqualsAndHashCode(exclude = "pointId")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "point")
public class Point {

    @EmbeddedId
    private PointId pointId;
    @Column(name = "time")
    private int time;
    @Column(name = "zoneid")
    private int zoneId;
    @Column(name = "zonelocalid")
    private int zoneLocalId;
    @Column(name = "accountstart")
    private LocalDateTime accountStart;
    @Column(name = "lastlogin")
    private LocalDateTime lastLogin;
    @Column(name = "enddate")
    private LocalDateTime endDate;
}
