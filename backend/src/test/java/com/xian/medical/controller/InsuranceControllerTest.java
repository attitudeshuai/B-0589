package com.xian.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.common.Result;
import com.xian.medical.entity.Insurance;
import com.xian.medical.service.InsuranceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private InsuranceService insuranceService;

    @InjectMocks
    private InsuranceController insuranceController;

    @Test
    void should_returnInsurance_when_studentHasInsurance() {
        Insurance insurance = new Insurance();
        insurance.setId(1L);
        insurance.setStudentId(100L);
        insurance.setStatus(1);
        insurance.setStartYear("2024");
        insurance.setDuration(1);
        insurance.setAmount(new BigDecimal("50"));

        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(insurance);

        Result<Insurance> result = insuranceController.getByStudentId(100L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(100L, result.getData().getStudentId());
        assertEquals(1, result.getData().getStatus());
        verify(insuranceService).getOne(any(QueryWrapper.class));
    }

    @Test
    void should_returnNullData_when_studentHasNoInsurance() {
        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(null);

        Result<Insurance> result = insuranceController.getByStudentId(999L);

        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }

    @Test
    void should_returnUninsuredRecord_when_studentStatusIsZero() {
        Insurance insurance = new Insurance();
        insurance.setId(2L);
        insurance.setStudentId(200L);
        insurance.setStatus(0);

        when(insuranceService.getOne(any(QueryWrapper.class))).thenReturn(insurance);

        Result<Insurance> result = insuranceController.getByStudentId(200L);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().getStatus());
    }

    @Test
    void should_saveNewInsurance_when_idIsNull() {
        Insurance newInsurance = new Insurance();
        newInsurance.setStudentId(100L);
        newInsurance.setStatus(1);
        newInsurance.setStartYear("2024");
        newInsurance.setDuration(1);
        newInsurance.setAmount(new BigDecimal("50"));

        when(insuranceService.save(any(Insurance.class))).thenReturn(true);

        Result<Boolean> result = insuranceController.update(newInsurance);

        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        verify(insuranceService).save(newInsurance);
        verify(insuranceService, never()).updateById(any());
    }

    @Test
    void should_updateExistingInsurance_when_idIsNotNull() {
        Insurance existing = new Insurance();
        existing.setId(1L);
        existing.setStudentId(100L);
        existing.setStatus(0);

        when(insuranceService.updateById(any(Insurance.class))).thenReturn(true);

        Result<Boolean> result = insuranceController.update(existing);

        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        verify(insuranceService).updateById(existing);
        verify(insuranceService, never()).save(any());
    }

    @Test
    void should_returnFalse_when_saveNewInsuranceFails() {
        Insurance newInsurance = new Insurance();
        newInsurance.setStudentId(100L);
        newInsurance.setStatus(1);

        when(insuranceService.save(any(Insurance.class))).thenReturn(false);

        Result<Boolean> result = insuranceController.update(newInsurance);

        assertEquals(200, result.getCode());
        assertFalse(result.getData());
    }

    @Test
    void should_returnFalse_when_updateExistingInsuranceFails() {
        Insurance existing = new Insurance();
        existing.setId(1L);
        existing.setStatus(0);

        when(insuranceService.updateById(any(Insurance.class))).thenReturn(false);

        Result<Boolean> result = insuranceController.update(existing);

        assertEquals(200, result.getCode());
        assertFalse(result.getData());
    }

    @Test
    void should_returnStats_when_dataExists() {
        when(insuranceService.count(any(QueryWrapper.class)))
                .thenReturn(5L)
                .thenReturn(3L);

        Result<Map<String, Integer>> result = insuranceController.getStats();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(5, result.getData().get("insured"));
        assertEquals(3, result.getData().get("uninsured"));
    }

    @Test
    void should_returnZeroStats_when_noData() {
        when(insuranceService.count(any(QueryWrapper.class)))
                .thenReturn(0L)
                .thenReturn(0L);

        Result<Map<String, Integer>> result = insuranceController.getStats();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().get("insured"));
        assertEquals(0, result.getData().get("uninsured"));
    }

    @Test
    void should_returnCorrectStats_when_onlyInsuredExist() {
        when(insuranceService.count(any(QueryWrapper.class)))
                .thenReturn(10L)
                .thenReturn(0L);

        Result<Map<String, Integer>> result = insuranceController.getStats();

        assertEquals(10, result.getData().get("insured"));
        assertEquals(0, result.getData().get("uninsured"));
    }

    @Test
    void should_returnAllInsurances_when_dataExists() {
        Insurance i1 = new Insurance();
        i1.setId(1L);
        i1.setStudentId(100L);
        i1.setStatus(1);

        Insurance i2 = new Insurance();
        i2.setId(2L);
        i2.setStudentId(200L);
        i2.setStatus(0);

        when(insuranceService.list()).thenReturn(Arrays.asList(i1, i2));

        Result<List<Insurance>> result = insuranceController.list();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals(1, result.getData().get(0).getStatus());
        assertEquals(0, result.getData().get(1).getStatus());
    }

    @Test
    void should_returnEmptyList_when_noData() {
        when(insuranceService.list()).thenReturn(Collections.emptyList());

        Result<List<Insurance>> result = insuranceController.list();

        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void should_callCountTwice_when_getStats() {
        when(insuranceService.count(any(QueryWrapper.class)))
                .thenReturn(1L)
                .thenReturn(2L);

        insuranceController.getStats();

        verify(insuranceService, times(2)).count(any(QueryWrapper.class));
    }
}
