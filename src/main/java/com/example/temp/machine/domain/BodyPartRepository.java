package com.example.temp.machine.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyPartRepository extends JpaRepository<BodyPart, Long> {

    List<BodyPart> findAllByNameIn(List<String> names);
}
