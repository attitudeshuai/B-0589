package com.xian.medical.controller;

import com.xian.medical.common.Result;
import com.xian.medical.entity.User;
import com.xian.medical.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void should_returnSuccessWithUser_when_validLogin() {
        User input = new User();
        input.setUsername("admin");
        input.setPassword("123456");
        input.setRole("ADMIN");

        User dbUser = new User();
        dbUser.setId(1L);
        dbUser.setUsername("admin");
        dbUser.setPassword("123456");
        dbUser.setRole("ADMIN");

        when(userService.login("admin", "123456", "ADMIN")).thenReturn(dbUser);

        Result<User> result = userController.login(input);

        assertEquals(200, result.getCode());
        assertEquals("Success", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        assertEquals("admin", result.getData().getUsername());
        verify(userService).login("admin", "123456", "ADMIN");
    }

    @Test
    void should_returnError_when_loginWithWrongCredentials() {
        User input = new User();
        input.setUsername("admin");
        input.setPassword("wrong");
        input.setRole("ADMIN");

        when(userService.login("admin", "wrong", "ADMIN")).thenReturn(null);

        Result<User> result = userController.login(input);

        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
        verify(userService).login("admin", "wrong", "ADMIN");
    }

    @Test
    void should_returnError_when_loginWithNullFields() {
        User input = new User();

        when(userService.login(null, null, null)).thenReturn(null);

        Result<User> result = userController.login(input);

        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void should_returnError_when_loginWithEmptyStrings() {
        User input = new User();
        input.setUsername("");
        input.setPassword("");
        input.setRole("");

        when(userService.login("", "", "")).thenReturn(null);

        Result<User> result = userController.login(input);

        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void should_returnSuccess_when_updatePasswordSucceeds() {
        User input = new User();
        input.setId(1L);
        input.setPassword("newpassword");

        when(userService.updateById(any(User.class))).thenReturn(true);

        Result<Boolean> result = userController.updatePassword(input);

        assertEquals(200, result.getCode());
        assertEquals("Success", result.getMessage());
        assertTrue(result.getData());
        verify(userService).updateById(input);
    }

    @Test
    void should_returnError_when_updatePasswordFails() {
        User input = new User();
        input.setId(1L);
        input.setPassword("newpassword");

        when(userService.updateById(any(User.class))).thenReturn(false);

        Result<Boolean> result = userController.updatePassword(input);

        assertEquals(500, result.getCode());
        assertEquals("修改失败", result.getMessage());
        assertNull(result.getData());
        verify(userService).updateById(input);
    }

    @Test
    void should_callUpdateByIdWithCorrectUser_when_updatePassword() {
        User input = new User();
        input.setId(2L);
        input.setUsername("student1");
        input.setPassword("newpass123");
        input.setRole("STUDENT");

        when(userService.updateById(any(User.class))).thenReturn(true);

        userController.updatePassword(input);

        verify(userService).updateById(input);
    }

    @Test
    void should_returnSuccessWithStudent_when_studentLogin() {
        User input = new User();
        input.setUsername("student1");
        input.setPassword("pass");
        input.setRole("STUDENT");

        User dbUser = new User();
        dbUser.setId(2L);
        dbUser.setUsername("student1");
        dbUser.setRole("STUDENT");

        when(userService.login("student1", "pass", "STUDENT")).thenReturn(dbUser);

        Result<User> result = userController.login(input);

        assertEquals(200, result.getCode());
        assertEquals("STUDENT", result.getData().getRole());
    }

    @Test
    void should_returnSuccessWithStaff_when_staffLogin() {
        User input = new User();
        input.setUsername("staff1");
        input.setPassword("pass");
        input.setRole("STAFF");

        User dbUser = new User();
        dbUser.setId(3L);
        dbUser.setUsername("staff1");
        dbUser.setRole("STAFF");

        when(userService.login("staff1", "pass", "STAFF")).thenReturn(dbUser);

        Result<User> result = userController.login(input);

        assertEquals(200, result.getCode());
        assertEquals("STAFF", result.getData().getRole());
    }
}
