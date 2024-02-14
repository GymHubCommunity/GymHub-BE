package com.example.temp.machine.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.temp.admin.dto.request.BodyPartCreateRequest;
import com.example.temp.admin.dto.request.MachineCreateRequest;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.dto.response.BodyPartCreateResponse;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.assertj.core.api.Assertions;
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

    @Test
    @DisplayName("이미 등록된 신체부위를 또 등록할 수 없다.")
    void createBodyPartFailDuplicatedName() throws Exception {
        // given
        String name = "등";
        saveBodyPart(name);
        BodyPartCreateRequest request = new BodyPartCreateRequest(name);

        // when
        Assertions.assertThatThrownBy(() -> machineService.createBodyPart(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.BODY_PART_ALREADY_REGISTER.getMessage());
    }

    @Test
    @DisplayName("운동기구를 등록한다.")
    void createMachine() throws Exception {
        // given
        String name = "벤치프레스";
        BodyPart bodyPart = saveBodyPart("등");
        MachineCreateRequest request = new MachineCreateRequest(name, List.of(bodyPart.getName()));

        // when
        MachineCreateResponse result = machineService.createMachine(request);

        // then
        em.flush();
        em.clear();
        Machine createdMachine = em.find(Machine.class, result.id());

        assertThat(createdMachine.getName()).isEqualTo(name);
        assertThat(createdMachine.getMachineBodyParts()).hasSize(1);
        assertThat(createdMachine.getMachineBodyParts().get(0).getBodyPart().getName()).isEqualTo(bodyPart.getName());
    }

    private BodyPart saveBodyPart(String name) {
        BodyPart bodyPart = BodyPart.builder()
            .name(name)
            .build();
        em.persist(bodyPart);
        return bodyPart;
    }

}