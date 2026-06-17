package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.Insurance;
import com.xian.medical.service.InsuranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsuranceControllerTest {

    @InjectMocks
    private InsuranceController insuranceController;

    @Mock
    private InsuranceService insuranceService;

    @Nested
    @DisplayName("getByStudentId")
    class GetByStudentIdTests {

        @Test
        void should_return_insurance_when_student_has_insurance() {
            Insurance insurance = buildInsurance(1L, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            when(insuranceService.getOne(any(Wrapper.class))).thenReturn(insurance);

            Result<Insurance> result = insuranceController.getByStudentId(100L);

            assertEquals(200, result.getCode());
            assertEquals(100L, result.getData().getStudentId());
            assertEquals(1, result.getData().getStatus());
            verify(insuranceService).getOne(any(Wrapper.class));
        }

        @Test
        void should_return_null_when_student_has_no_insurance() {
            when(insuranceService.getOne(any(Wrapper.class))).thenReturn(null);

            Result<Insurance> result = insuranceController.getByStudentId(999L);

            assertEquals(200, result.getCode());
            assertNull(result.getData());
        }

        @Test
        void should_return_insurance_with_correct_fields_when_student_is_uninsured() {
            Insurance insurance = buildInsurance(2L, 200L, 0, "2024", 1, BigDecimal.ZERO);
            when(insuranceService.getOne(any(Wrapper.class))).thenReturn(insurance);

            Result<Insurance> result = insuranceController.getByStudentId(200L);

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().getStatus());
        }

        @Test
        void should_return_insurance_when_student_id_is_zero() {
            Insurance insurance = buildInsurance(3L, 0L, 1, "2024", 1, new BigDecimal("50.00"));
            when(insuranceService.getOne(any(Wrapper.class))).thenReturn(insurance);

            Result<Insurance> result = insuranceController.getByStudentId(0L);

            assertEquals(200, result.getCode());
            assertEquals(0L, result.getData().getStudentId());
        }

        @Test
        void should_throw_exception_when_service_throws_runtime_exception() {
            when(insuranceService.getOne(any(Wrapper.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class,
                    () -> insuranceController.getByStudentId(100L));
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Test
        void should_return_success_true_when_new_insurance_is_saved() {
            Insurance insurance = buildInsurance(null, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            when(insuranceService.save(insurance)).thenReturn(true);

            Result<Boolean> result = insuranceController.update(insurance);

            assertEquals(200, result.getCode());
            assertTrue(result.getData());
            verify(insuranceService).save(insurance);
        }

        @Test
        void should_return_success_true_when_existing_insurance_is_updated() {
            Insurance insurance = buildInsurance(1L, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            when(insuranceService.updateById(insurance)).thenReturn(true);

            Result<Boolean> result = insuranceController.update(insurance);

            assertEquals(200, result.getCode());
            assertTrue(result.getData());
            verify(insuranceService).updateById(insurance);
        }

        @Test
        void should_return_success_when_existing_insurance_is_cancelled() {
            Insurance insurance = buildInsurance(1L, 100L, 0, "2024", 1, BigDecimal.ZERO);
            when(insuranceService.updateById(insurance)).thenReturn(true);

            Result<Boolean> result = insuranceController.update(insurance);

            assertEquals(200, result.getCode());
            assertTrue(result.getData());
        }

        @Test
        void should_return_success_false_when_update_fails() {
            Insurance insurance = buildInsurance(1L, 100L, 0, "2024", 1, BigDecimal.ZERO);
            when(insuranceService.updateById(insurance)).thenReturn(false);

            Result<Boolean> result = insuranceController.update(insurance);

            assertEquals(200, result.getCode());
            assertFalse(result.getData());
        }

        @Test
        void should_return_success_false_when_save_fails() {
            Insurance insurance = buildInsurance(null, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            when(insuranceService.save(insurance)).thenReturn(false);

            Result<Boolean> result = insuranceController.update(insurance);

            assertEquals(200, result.getCode());
            assertFalse(result.getData());
        }

        @Test
        void should_throw_exception_when_service_throws_on_save() {
            Insurance insurance = buildInsurance(null, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            when(insuranceService.save(any(Insurance.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> insuranceController.update(insurance));
        }

        @Test
        void should_throw_exception_when_service_throws_on_update() {
            Insurance insurance = buildInsurance(1L, 100L, 0, "2024", 1, BigDecimal.ZERO);
            when(insuranceService.updateById(any(Insurance.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> insuranceController.update(insurance));
        }
    }

    @Nested
    @DisplayName("getStats")
    class GetStatsTests {

        @Test
        void should_return_correct_stats_when_both_counts_are_positive() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn(150L)
                    .thenReturn(50L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            assertEquals(200, result.getCode());
            assertEquals(150, result.getData().get("insured"));
            assertEquals(50, result.getData().get("uninsured"));
        }

        @Test
        void should_return_zero_for_both_when_no_records_exist() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn(0L)
                    .thenReturn(0L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().get("insured"));
            assertEquals(0, result.getData().get("uninsured"));
        }

        @Test
        void should_return_zero_for_insured_when_none_are_insured() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn(0L)
                    .thenReturn(200L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            assertEquals(200, result.getCode());
            assertEquals(0, result.getData().get("insured"));
            assertEquals(200, result.getData().get("uninsured"));
        }

        @Test
        void should_return_zero_for_uninsured_when_all_are_insured() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn(200L)
                    .thenReturn(0L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            assertEquals(200, result.getCode());
            assertEquals(200, result.getData().get("insured"));
            assertEquals(0, result.getData().get("uninsured"));
        }

        @Test
        void should_return_map_with_exactly_two_keys() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn(100L)
                    .thenReturn(50L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            assertEquals(2, result.getData().size());
            assertTrue(result.getData().containsKey("insured"));
            assertTrue(result.getData().containsKey("uninsured"));
        }

        @Test
        void should_return_stats_with_total_equals_sum_of_insured_and_uninsured() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn(70L)
                    .thenReturn(30L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            int total = result.getData().get("insured") + result.getData().get("uninsured");
            assertEquals(100, total);
        }

        @Test
        void should_handle_large_counts_at_integer_max_boundary() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenReturn((long) Integer.MAX_VALUE)
                    .thenReturn(0L);

            Result<Map<String, Integer>> result = insuranceController.getStats();

            assertEquals(Integer.MAX_VALUE, result.getData().get("insured"));
            assertEquals(0, result.getData().get("uninsured"));
        }

        @Test
        void should_throw_exception_when_count_throws_runtime_exception() {
            when(insuranceService.count(any(Wrapper.class)))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> insuranceController.getStats());
        }
    }

    @Nested
    @DisplayName("list")
    class ListTests {

        @Test
        void should_return_list_of_all_insurance_records() {
            Insurance insurance1 = buildInsurance(1L, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            Insurance insurance2 = buildInsurance(2L, 101L, 0, "2024", 1, BigDecimal.ZERO);
            List<Insurance> insurances = Arrays.asList(insurance1, insurance2);
            when(insuranceService.list()).thenReturn(insurances);

            Result<List<Insurance>> result = insuranceController.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals(2, result.getData().size());
            verify(insuranceService).list();
        }

        @Test
        void should_return_empty_list_when_no_records_exist() {
            when(insuranceService.list()).thenReturn(Collections.emptyList());

            Result<List<Insurance>> result = insuranceController.list();

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isEmpty());
        }

        @Test
        void should_return_list_with_single_record_when_only_one_exists() {
            Insurance insurance = buildInsurance(1L, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            List<Insurance> insurances = Collections.singletonList(insurance);
            when(insuranceService.list()).thenReturn(insurances);

            Result<List<Insurance>> result = insuranceController.list();

            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().size());
            assertEquals(100L, result.getData().get(0).getStudentId());
        }

        @Test
        void should_return_list_containing_both_insured_and_uninsured() {
            Insurance insured = buildInsurance(1L, 100L, 1, "2024", 1, new BigDecimal("100.00"));
            Insurance uninsured = buildInsurance(2L, 101L, 0, "2024", 1, BigDecimal.ZERO);
            List<Insurance> insurances = Arrays.asList(insured, uninsured);
            when(insuranceService.list()).thenReturn(insurances);

            Result<List<Insurance>> result = insuranceController.list();

            assertEquals(200, result.getCode());
            assertEquals(2, result.getData().size());
            assertEquals(1, result.getData().get(0).getStatus());
            assertEquals(0, result.getData().get(1).getStatus());
        }

        @Test
        void should_throw_exception_when_list_throws_runtime_exception() {
            when(insuranceService.list())
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> insuranceController.list());
        }
    }

    private Insurance buildInsurance(Long id, Long studentId, Integer status,
                                     String startYear, Integer duration, BigDecimal amount) {
        Insurance insurance = new Insurance();
        insurance.setId(id);
        insurance.setStudentId(studentId);
        insurance.setStatus(status);
        insurance.setStartYear(startYear);
        insurance.setDuration(duration);
        insurance.setAmount(amount);
        return insurance;
    }
}
