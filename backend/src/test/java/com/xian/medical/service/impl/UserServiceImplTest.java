package com.xian.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Spy
    private UserServiceImpl userService;

    @Test
    void should_returnUser_when_validCredentials() {
        User expected = new User();
        expected.setId(1L);
        expected.setUsername("admin");
        expected.setPassword("123456");
        expected.setRole("ADMIN");

        doReturn(expected).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("admin", "123456", "ADMIN");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        verify(userService).getOne(any(QueryWrapper.class));
    }

    @Test
    void should_returnNull_when_credentialsNotMatch() {
        doReturn(null).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("wrong", "wrong", "ADMIN");

        assertNull(result);
        verify(userService).getOne(any(QueryWrapper.class));
    }

    @Test
    void should_returnNull_when_emptyCredentials() {
        doReturn(null).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("", "", "");

        assertNull(result);
    }

    @Test
    void should_returnNull_when_nullCredentials() {
        doReturn(null).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login(null, null, null);

        assertNull(result);
    }

    @Test
    void should_buildQueryWithAllThreeFields_when_loginCalled() {
        ArgumentCaptor<QueryWrapper<User>> captor = ArgumentCaptor.forClass(QueryWrapper.class);
        doReturn(null).when(userService).getOne(captor.capture());

        userService.login("testuser", "testpass", "STUDENT");

        QueryWrapper<User> wrapper = captor.getValue();
        String sqlSegment = wrapper.getCustomSqlSegment();
        assertTrue(sqlSegment.contains("username"));
        assertTrue(sqlSegment.contains("password"));
        assertTrue(sqlSegment.contains("role"));
    }

    @Test
    void should_returnUserWithStudentRole_when_studentLogin() {
        User student = new User();
        student.setId(2L);
        student.setUsername("student1");
        student.setRole("STUDENT");

        doReturn(student).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("student1", "pass", "STUDENT");

        assertNotNull(result);
        assertEquals("STUDENT", result.getRole());
    }

    @Test
    void should_returnUserWithStaffRole_when_staffLogin() {
        User staff = new User();
        staff.setId(3L);
        staff.setUsername("staff1");
        staff.setRole("STAFF");

        doReturn(staff).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("staff1", "pass", "STAFF");

        assertNotNull(result);
        assertEquals("STAFF", result.getRole());
    }

    @Test
    void should_callGetOneExactlyOnce_when_loginCalled() {
        doReturn(null).when(userService).getOne(any(QueryWrapper.class));

        userService.login("a", "b", "c");

        verify(userService, times(1)).getOne(any(QueryWrapper.class));
    }

    @Test
    void should_returnNull_when_passwordIsWrongButOthersCorrect() {
        doReturn(null).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("admin", "wrongpassword", "ADMIN");

        assertNull(result);
    }

    @Test
    void should_returnNull_when_roleIsWrongButOthersCorrect() {
        doReturn(null).when(userService).getOne(any(QueryWrapper.class));

        User result = userService.login("admin", "123456", "WRONG_ROLE");

        assertNull(result);
    }
}
