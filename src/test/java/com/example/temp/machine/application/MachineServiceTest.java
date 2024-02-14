package com.example.temp.machine.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.admin.dto.request.BodyPartCreateRequest;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.dto.response.BodyPartCreateResponse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MachineServiceTest {

    @Autowired
    MachineService machineService;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("신체부위를 등록한다.")
    void createBodyPart() throws Exception {
        // given
        String name = "등";
        BodyPartCreateRequest request = new BodyPartCreateRequest(name);

        // when
        BodyPartCreateResponse response = machineService.createBodyPart(request);
        em.flush();
        em.clear();

        // then
        BodyPart bodyPart = em.find(BodyPart.class, response.id());
        assertThat(bodyPart.getName()).isEqualTo(name);
        assertThat(response.name()).isEqualTo(name);
    }

}