package com.xian.medical.controller;

import com.xian.medical.common.Result;
import com.xian.medical.entity.User;
import com.xian.medical.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        void should_return_success_with_user_when_login_credentials_are_correct() {
            User requestUser = buildUser(null, "admin", "admin123", "ADMIN");
            User returnedUser = buildUser(1L, "admin", "admin123", "ADMIN");
            when(userService.login("admin", "admin123", "ADMIN")).thenReturn(returnedUser);

            Result<User> result = userController.login(requestUser);

            assertEquals(200, result.getCode());
            assertEquals("Success", result.getMessage());
            assertNotNull(result.getData());
            assertEquals(1L, result.getData().getId());
            assertEquals("admin", result.getData().getUsername());
            verify(userService).login("admin", "admin123", "ADMIN");
        }

        @Test
        void should_return_error_when_login_credentials_are_wrong() {
            User requestUser = buildUser(null, "admin", "wrongpass", "ADMIN");
            when(userService.login("admin", "wrongpass", "ADMIN")).thenReturn(null);

            Result<User> result = userController.login(requestUser);

            assertEquals(500, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        void should_return_success_with_student_when_student_login_is_correct() {
            User requestUser = buildUser(null, "student01", "pwd123", "STUDENT");
            User returnedUser = buildUser(2L, "student01", "pwd123", "STUDENT");
            when(userService.login("student01", "pwd123", "STUDENT")).thenReturn(returnedUser);

            Result<User> result = userController.login(requestUser);

            assertEquals(200, result.getCode());
            assertEquals("student01", result.getData().getUsername());
            assertEquals("STUDENT", result.getData().getRole());
        }

        @Test
        void should_return_success_with_staff_when_staff_login_is_correct() {
            User requestUser = buildUser(null, "staff01", "pwd456", "STAFF");
            User returnedUser = buildUser(3L, "staff01", "pwd456", "STAFF");
            when(userService.login("staff01", "pwd456", "STAFF")).thenReturn(returnedUser);

            Result<User> result = userController.login(requestUser);

            assertEquals(200, result.getCode());
            assertEquals("staff01", result.getData().getUsername());
            assertEquals("STAFF", result.getData().getRole());
        }

        @Test
        void should_return_error_when_username_is_wrong_only() {
            User requestUser = buildUser(null, "wronguser", "pwd123", "STUDENT");
            when(userService.login("wronguser", "pwd123", "STUDENT")).thenReturn(null);

            Result<User> result = userController.login(requestUser);

            assertEquals(500, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
        }

        @Test
        void should_return_error_when_password_is_wrong_only() {
            User requestUser = buildUser(null, "student01", "wrongpass", "STUDENT");
            when(userService.login("student01", "wrongpass", "STUDENT")).thenReturn(null);

            Result<User> result = userController.login(requestUser);

            assertEquals(500, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
        }

        @Test
        void should_return_error_when_user_fields_are_empty_strings() {
            User requestUser = buildUser(null, "", "", "");
            when(userService.login("", "", "")).thenReturn(null);

            Result<User> result = userController.login(requestUser);

            assertEquals(500, result.getCode());
            assertEquals("用户名或密码错误", result.getMessage());
        }

        @Test
        void should_return_success_with_user_when_login_with_boundary_username() {
            String boundaryUsername = "a".repeat(50);
            User requestUser = buildUser(null, boundaryUsername, "pwd123", "STUDENT");
            User returnedUser = buildUser(4L, boundaryUsername, "pwd123", "STUDENT");
            when(userService.login(boundaryUsername, "pwd123", "STUDENT")).thenReturn(returnedUser);

            Result<User> result = userController.login(requestUser);

            assertEquals(200, result.getCode());
            assertEquals(boundaryUsername, result.getData().getUsername());
        }

        @Test
        void should_throw_exception_when_service_throws_runtime_exception() {
            User requestUser = buildUser(null, "admin", "admin123", "ADMIN");
            when(userService.login("admin", "admin123", "ADMIN"))
                    .thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> userController.login(requestUser));
        }
    }

    @Nested
    @DisplayName("updatePassword")
    class UpdatePasswordTests {

        @Test
        void should_return_success_true_when_password_update_succeeds() {
            User user = buildUser(1L, "admin", "newpass123", "ADMIN");
            when(userService.updateById(user)).thenReturn(true);

            Result<Boolean> result = userController.updatePassword(user);

            assertEquals(200, result.getCode());
            assertEquals("Success", result.getMessage());
            assertTrue(result.getData());
            verify(userService).updateById(user);
        }

        @Test
        void should_return_error_when_password_update_fails() {
            User user = buildUser(1L, "admin", "newpass123", "ADMIN");
            when(userService.updateById(user)).thenReturn(false);

            Result<Boolean> result = userController.updatePassword(user);

            assertEquals(500, result.getCode());
            assertEquals("修改失败", result.getMessage());
            assertNull(result.getData());
        }

        @Test
        void should_return_success_when_updating_with_existing_user_id() {
            User user = buildUser(2L, "student01", "updatedpwd", "STUDENT");
            when(userService.updateById(user)).thenReturn(true);

            Result<Boolean> result = userController.updatePassword(user);

            assertEquals(200, result.getCode());
            assertTrue(result.getData());
        }

        @Test
        void should_return_error_when_user_id_is_null() {
            User user = buildUser(null, "newuser", "pwd", "ADMIN");
            when(userService.updateById(user)).thenReturn(false);

            Result<Boolean> result = userController.updatePassword(user);

            assertEquals(500, result.getCode());
            assertEquals("修改失败", result.getMessage());
        }

        @Test
        void should_throw_exception_when_service_throws_runtime_exception() {
            User user = buildUser(1L, "admin", "newpass", "ADMIN");
            when(userService.updateById(user))
                    .thenThrow(new RuntimeException("DB connection error"));

            assertThrows(RuntimeException.class, () -> userController.updatePassword(user));
        }

        @Test
        void should_return_success_true_when_updating_to_same_password() {
            User user = buildUser(1L, "admin", "samepass", "ADMIN");
            when(userService.updateById(user)).thenReturn(true);

            Result<Boolean> result = userController.updatePassword(user);

            assertEquals(200, result.getCode());
            assertTrue(result.getData());
        }
    }

    private User buildUser(Long id, String username, String password, String role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
