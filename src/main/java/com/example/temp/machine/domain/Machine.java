package com.example.temp.machine.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    @Builder
    private Machine(String name, List<MachineBodyPart> machineBodyParts) {
        this.name = name;
        if (machineBodyParts != null) {
            this.machineBodyParts = new ArrayList<>();
            this.machineBodyParts.addAll(machineBodyParts);
        }
    }

    public static Machine create(String name, List<BodyPart> bodyParts) {
        Machine machine = Machine.builder()
            .name(name)
            .build();
        createMachineBodyParts(bodyParts)
            .forEach(machineBodyPart -> machineBodyPart.relate(machine));
        return machine;
    }

    private static List<MachineBodyPart> createMachineBodyParts(List<BodyPart> bodyParts) {
        return bodyParts.stream()
            .map(MachineBodyPart::create)
            .toList();
    }
}
