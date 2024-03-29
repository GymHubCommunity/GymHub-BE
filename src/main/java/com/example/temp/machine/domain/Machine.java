package com.example.temp.machine.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "machines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machine_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MachineBodyPart> machineBodyParts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyPart majorBodyPart;

    @Builder
    private Machine(String name, List<MachineBodyPart> machineBodyParts, BodyPart majorBodyPart) {
        Objects.requireNonNull(machineBodyParts);
        this.name = name;
        this.machineBodyParts = new ArrayList<>();
        machineBodyParts.forEach(machineBodyPart -> machineBodyPart.relate(this));
        this.majorBodyPart = majorBodyPart;
    }

    public static Machine create(String name, List<BodyPart> bodyParts) {
        return Machine.builder()
            .name(name)
            .machineBodyParts(createMachineBodyParts(bodyParts))
            .majorBodyPart(bodyParts.get(0))
            .build();
    }

    private static List<MachineBodyPart> createMachineBodyParts(List<BodyPart> bodyParts) {
        return bodyParts.stream()
            .map(MachineBodyPart::create)
            .toList();
    }
}
