package com.example.temp.machine.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "machine_bodyparts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MachineBodyPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_bodypart_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Machine machine;

    private BodyPart bodyPart;

    @Builder
    private MachineBodyPart(Machine machine, BodyPart bodyPart) {
        this.machine = machine;
        this.bodyPart = bodyPart;
    }

    public static MachineBodyPart create(BodyPart bodyPart) {
        return MachineBodyPart.builder()
            .bodyPart(bodyPart)
            .build();
    }
}
