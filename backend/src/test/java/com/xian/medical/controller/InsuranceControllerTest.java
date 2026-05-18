package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.Insurance;
import com.xian.medical.service.InsuranceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsuranceControllerTest {

    @Mock
    private InsuranceService insuranceService;

    @InjectMocks
    private InsuranceController insuranceController;

    private Insurance mockInsurance;

    @BeforeEach
    void setUp() {
        mockInsurance = new Insurance();
        mockInsurance.setId(1L);
        mockInsurance.setStudentId(100L);
        mockInsurance.setStatus(1);
        mockInsurance.setStartYear("2024");
        mockInsurance.setDuration(1);
        mockInsurance.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void should_return_insurance_when_get_by_existing_student_id() {
        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(mockInsurance);

        Result<Insurance> result = insuranceController.getByStudentId(100L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(100L, result.getData().getStudentId());
        assertEquals(1, result.getData().getStatus());
        verify(insuranceService, times(1)).getOne(any(QueryWrapper.class));
    }

    @Test
    void should_return_null_data_when_get_by_nonexistent_student_id() {
        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(null);

        Result<Insurance> result = insuranceController.getByStudentId(999L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
        verify(insuranceService, times(1)).getOne(any(QueryWrapper.class));
    }

    @Test
    void should_return_success_when_creating_new_insurance() {
        Insurance newInsurance = new Insurance();
        newInsurance.setStudentId(200L);
        newInsurance.setStatus(1);
        newInsurance.setStartYear("2024");
        newInsurance.setDuration(1);
        newInsurance.setAmount(new BigDecimal("100.00"));

        when(insuranceService.save(newInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(newInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        verify(insuranceService, times(1)).save(newInsurance);
        verify(insuranceService, never()).updateById(any(Insurance.class));
    }

    @Test
    void should_return_success_when_updating_existing_insurance() {
        mockInsurance.setStatus(0);

        when(insuranceService.updateById(mockInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(mockInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        verify(insuranceService, times(1)).updateById(mockInsurance);
        verify(insuranceService, never()).save(any(Insurance.class));
    }

    @Test
    void should_return_success_when_opt_out_insurance() {
        mockInsurance.setStatus(0);

        when(insuranceService.updateById(mockInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(mockInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        assertEquals(0, mockInsurance.getStatus());
    }

    @Test
    void should_return_success_when_opt_in_insurance() {
        mockInsurance.setStatus(1);

        when(insuranceService.updateById(mockInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(mockInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        assertEquals(1, mockInsurance.getStatus());
    }

    @Test
    void should_return_stats_with_correct_counts() {
        when(insuranceService.count(any(QueryWrapper.class))).thenReturn(50L, 30L);

        Result<Map<String, Integer>> result = insuranceController.getStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(50, result.getData().get("insured"));
        assertEquals(30, result.getData().get("uninsured"));
        verify(insuranceService, times(2)).count(any(QueryWrapper.class));
    }

    @Test
    void should_return_zero_stats_when_no_insurance_records() {
        when(insuranceService.count(any(QueryWrapper.class))).thenReturn(0L, 0L);

        Result<Map<String, Integer>> result = insuranceController.getStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().get("insured"));
        assertEquals(0, result.getData().get("uninsured"));
    }

    @Test
    void should_return_all_insurance_list() {
        List<Insurance> insuranceList = new ArrayList<>();
        insuranceList.add(mockInsurance);

        Insurance insurance2 = new Insurance();
        insurance2.setId(2L);
        insurance2.setStudentId(200L);
        insurance2.setStatus(0);
        insuranceList.add(insurance2);

        when(insuranceService.list()).thenReturn(insuranceList);

        Result<List<Insurance>> result = insuranceController.list();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals(100L, result.getData().get(0).getStudentId());
        assertEquals(200L, result.getData().get(1).getStudentId());
        verify(insuranceService, times(1)).list();
    }

    @Test
    void should_return_empty_list_when_no_insurance_records() {
        when(insuranceService.list()).thenReturn(new ArrayList<>());

        Result<List<Insurance>> result = insuranceController.list();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void should_return_uninsured_insurance_when_status_is_zero() {
        mockInsurance.setStatus(0);
        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(mockInsurance);

        Result<Insurance> result = insuranceController.getByStudentId(100L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().getStatus());
    }

    @Test
    void should_throw_exception_when_update_insurance_is_null() {
        assertThrows(NullPointerException.class, () -> insuranceController.update(null));
    }

    @Test
    void should_handle_very_large_student_id() {
        Long largeStudentId = 999999999L;
        mockInsurance.setStudentId(largeStudentId);
        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(mockInsurance);

        Result<Insurance> result = insuranceController.getByStudentId(largeStudentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(largeStudentId, result.getData().getStudentId());
    }

    @Test
    void should_handle_zero_student_id() {
        Long zeroStudentId = 0L;
        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(null);

        Result<Insurance> result = insuranceController.getByStudentId(zeroStudentId);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void should_return_success_when_saving_insurance_with_minimal_fields() {
        Insurance minimalInsurance = new Insurance();
        minimalInsurance.setStudentId(300L);
        minimalInsurance.setStatus(1);

        when(insuranceService.save(minimalInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(minimalInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
    }

    @Test
    void should_handle_negative_duration_in_insurance() {
        mockInsurance.setDuration(-1);
        when(insuranceService.updateById(mockInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(mockInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
    }

    @Test
    void should_handle_zero_duration_in_insurance() {
        mockInsurance.setDuration(0);
        when(insuranceService.updateById(mockInsurance)).thenReturn(true);

        Result<Boolean> result = insuranceController.update(mockInsurance);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
    }
}
