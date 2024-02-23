package com.example.temp.machine.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    boolean existsByName(String name);

    @Query("SELECT m FROM Machine m JOIN FETCH m.machineBodyParts mb"
        + " WHERE mb.bodyPart = :bodypart")
    List<Machine> findAllByBodyPart(@Param("bodypart") BodyPart bodyPart);

    @Query("SELECT m From Machine m JOIN m.machineBodyParts mb"
        + " WHERE mb.bodyPart in (:bodyParts)")
    List<Machine> findAllBelongTo(@Param("bodyParts") List<BodyPart> bodyParts);

    List<Machine> findAllByNameIn(List<String> machineNames);

}
