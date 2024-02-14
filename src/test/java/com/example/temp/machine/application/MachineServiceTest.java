package com.example.temp.machine.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.temp.machine.domain.MachineBodyPart;
import com.example.temp.machine.dto.request.MachineBulkCreateRequest;
import com.example.temp.machine.dto.request.MachineCreateRequest;
import com.example.temp.common.exception.ApiException;
import com.example.temp.common.exception.ErrorCode;
import com.example.temp.machine.domain.BodyPart;
import com.example.temp.machine.domain.Machine;
import com.example.temp.machine.dto.request.MachineSearchUsingBodyPartRequest;
import com.example.temp.machine.dto.response.MachineCreateResponse;
import com.example.temp.machine.dto.response.MachineInfo;
import jakarta.persistence.EntityManager;
import java.util.List;
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
    @DisplayName("운동기구를 등록한다.")
    void createMachine() throws Exception {
        // given
        String name = "벤치프레스";
        BodyPart bodyPart = BodyPart.BACK;
        MachineCreateRequest request = new MachineCreateRequest(name, List.of(bodyPart));

        // when
        MachineCreateResponse result = machineService.createMachine(request);

        // then
        em.flush();
        em.clear();
        Machine createdMachine = em.find(Machine.class, result.id());

        assertThat(createdMachine.getName()).isEqualTo(name);
        assertThat(createdMachine.getMachineBodyParts()).hasSize(1);
        assertThat(createdMachine.getMachineBodyParts().get(0).getBodyPart()).isEqualTo(bodyPart);
    }

    @Test
    @DisplayName("운동 기구는 한 개가 넘는 신체 부위에 매핑할 수 없다.")
    void machineMappedOnlyOneBodyPart() throws Exception {
        // given
        String name = "벤치프레스";
        BodyPart bodyPart1 = BodyPart.LEG;
        BodyPart bodyPart2 = BodyPart.SHOULDER;
        MachineCreateRequest request = new MachineCreateRequest(name,
            List.of(bodyPart1, bodyPart2));

        // when & then
        assertThatThrownBy(() -> machineService.createMachine(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MACHINE_MATCH_ONLY_ONE_BODY_PART.getMessage());
    }

    @Test
    @DisplayName("이미 등록된 운동기구를 또 등록할 수 없다.")
    void createMachineFailDuplicatedName() throws Exception {
        // given
        String name = "벤치프레스";
        saveMachine(name);
        MachineCreateRequest request = new MachineCreateRequest(name, List.of(BodyPart.BACK));

        // when & then
        assertThatThrownBy(() -> machineService.createMachine(request))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining(ErrorCode.MACHINE_ALREADY_REGISTER.getMessage());
    }

    @Test
    @DisplayName("벌크로 운동기구를 등록한다.")
    void createMachineBulk() throws Exception {
        // given
        BodyPart bodyPart = BodyPart.SHOULDER;

        MachineBulkCreateRequest request = new MachineBulkCreateRequest(List.of(
            new MachineCreateRequest("벤치프레스", List.of(bodyPart)),
            new MachineCreateRequest("아령", List.of(bodyPart))
        ));
        // when
        List<MachineCreateResponse> response = machineService.createMachinesBulk(request);

        // then
        em.flush();
        em.clear();
        assertThat(response).hasSize(2)
            .extracting("name")
            .contains("벤치프레스", "아령");
    }

    @Test
    @DisplayName("신체 부위에 해당하는 머신들을 가져온다.")
    void searchUsingBodyPart() throws Exception {
        // given
        BodyPart keyword = BodyPart.SHOULDER;
        Machine target = saveMachine("머신1", keyword);
        saveMachine("머신2", BodyPart.CARDIO);
        em.flush();
        em.clear();

        // when
        List<MachineInfo> results = machineService.searchUsingBodyPart(
            new MachineSearchUsingBodyPartRequest(keyword));

        // then
        assertThat(results).hasSize(1)
            .extracting("name")
            .contains(target.getName());
    }

    @Test
    @DisplayName("신체 부위에 해당하는 머신이 없으면 비어있는 리스트를 반환한다.")
    void searchUsingBodyPartThatResultIsEmpty() throws Exception {
        // given
        BodyPart keyword = BodyPart.BACK;
        saveMachine("머신1", BodyPart.SHOULDER);
        saveMachine("머신2", BodyPart.CARDIO);
        em.flush();
        em.clear();

        // when
        List<MachineInfo> results = machineService.searchUsingBodyPart(
            new MachineSearchUsingBodyPartRequest(keyword));

        // then
        assertThat(results).isEmpty();
    }

    private Machine saveMachine(String name, BodyPart bodyPart) {
        MachineBodyPart machineBodyPart = MachineBodyPart.create(bodyPart);
        Machine machine = Machine.builder()
            .name(name)
            .machineBodyParts(List.of(machineBodyPart))
            .build();
        machineBodyPart.setMachine(machine);
        em.persist(machine);
        return machine;
    }


    private Machine saveMachine(String name) {
        Machine machine = Machine.builder()
            .name(name)
            .build();
        em.persist(machine);
        return machine;
    }

}