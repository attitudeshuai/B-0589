package com.xian.medical.controller;

import com.xian.medical.common.Result;
import com.xian.medical.entity.User;
import com.xian.medical.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("testpass");
        mockUser.setRole("STUDENT");
    }

    @Test
    void should_return_success_with_user_when_login_successful() {
        when(userService.login("testuser", "testpass", "STUDENT")).thenReturn(mockUser);

        Result<User> result = userController.login(mockUser);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("Success", result.getMessage());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUsername());
        verify(userService, times(1)).login("testuser", "testpass", "STUDENT");
    }

    @Test
    void should_return_error_when_login_fails_due_to_wrong_credentials() {
        when(userService.login("testuser", "wrongpass", "STUDENT")).thenReturn(null);

        User loginUser = new User();
        loginUser.setUsername("testuser");
        loginUser.setPassword("wrongpass");
        loginUser.setRole("STUDENT");

        Result<User> result = userController.login(loginUser);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
        verify(userService, times(1)).login("testuser", "wrongpass", "STUDENT");
    }

    @Test
    void should_return_error_when_user_does_not_exist() {
        when(userService.login("nonexistent", "testpass", "STUDENT")).thenReturn(null);

        User loginUser = new User();
        loginUser.setUsername("nonexistent");
        loginUser.setPassword("testpass");
        loginUser.setRole("STUDENT");

        Result<User> result = userController.login(loginUser);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void should_return_error_when_role_is_incorrect() {
        when(userService.login("testuser", "testpass", "ADMIN")).thenReturn(null);

        User loginUser = new User();
        loginUser.setUsername("testuser");
        loginUser.setPassword("testpass");
        loginUser.setRole("ADMIN");

        Result<User> result = userController.login(loginUser);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户名或密码错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void should_return_success_with_true_when_update_password_successful() {
        when(userService.updateById(mockUser)).thenReturn(true);

        Result<Boolean> result = userController.updatePassword(mockUser);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
        verify(userService, times(1)).updateById(mockUser);
    }

    @Test
    void should_return_error_when_update_password_fails() {
        when(userService.updateById(mockUser)).thenReturn(false);

        Result<Boolean> result = userController.updatePassword(mockUser);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("修改失败", result.getMessage());
        assertNull(result.getData());
        verify(userService, times(1)).updateById(mockUser);
    }

    @Test
    void should_return_success_when_updating_password_with_minimal_user_info() {
        User userWithIdOnly = new User();
        userWithIdOnly.setId(1L);
        userWithIdOnly.setPassword("newpass");

        when(userService.updateById(userWithIdOnly)).thenReturn(true);

        Result<Boolean> result = userController.updatePassword(userWithIdOnly);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
    }

    @Test
    void should_throw_null_pointer_exception_when_login_user_is_null() {
        assertThrows(NullPointerException.class, () -> userController.login(null));
    }

    @Test
    void should_return_error_when_update_password_with_null_user() {
        when(userService.updateById(null)).thenReturn(false);

        Result<Boolean> result = userController.updatePassword(null);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("修改失败", result.getMessage());
    }

    @Test
    void should_return_success_for_staff_login() {
        mockUser.setRole("STAFF");
        when(userService.login("staffuser", "staffpass", "STAFF")).thenReturn(mockUser);

        User loginUser = new User();
        loginUser.setUsername("staffuser");
        loginUser.setPassword("staffpass");
        loginUser.setRole("STAFF");

        Result<User> result = userController.login(loginUser);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("STAFF", result.getData().getRole());
    }

    @Test
    void should_return_success_for_admin_login() {
        mockUser.setRole("ADMIN");
        when(userService.login("adminuser", "adminpass", "ADMIN")).thenReturn(mockUser);

        User loginUser = new User();
        loginUser.setUsername("adminuser");
        loginUser.setPassword("adminpass");
        loginUser.setRole("ADMIN");

        Result<User> result = userController.login(loginUser);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("ADMIN", result.getData().getRole());
    }
}
