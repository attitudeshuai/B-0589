package com.xian.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.xian.medical.entity.User;
import com.xian.medical.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);
    }

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        void should_return_user_when_correct_credentials_and_role() {
            User expected = buildUser(1L, "admin", "admin123", "ADMIN");
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(expected);

            User result = userService.login("admin", "admin123", "ADMIN");

            assertNotNull(result);
            assertEquals("admin", result.getUsername());
            assertEquals("ADMIN", result.getRole());
            verify(userMapper).selectOne(any(Wrapper.class), anyBoolean());
        }

        @Test
        void should_return_student_user_when_student_role_credentials_match() {
            User expected = buildUser(2L, "student01", "pwd123", "STUDENT");
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(expected);

            User result = userService.login("student01", "pwd123", "STUDENT");

            assertNotNull(result);
            assertEquals("student01", result.getUsername());
            assertEquals("STUDENT", result.getRole());
        }

        @Test
        void should_return_staff_user_when_staff_role_credentials_match() {
            User expected = buildUser(3L, "staff01", "pwd456", "STAFF");
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(expected);

            User result = userService.login("staff01", "pwd456", "STAFF");

            assertNotNull(result);
            assertEquals("staff01", result.getUsername());
            assertEquals("STAFF", result.getRole());
        }

        @Test
        void should_return_null_when_username_is_wrong() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("wronguser", "pwd123", "STUDENT");

            assertNull(result);
        }

        @Test
        void should_return_null_when_password_is_wrong() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("student01", "wrongpwd", "STUDENT");

            assertNull(result);
        }

        @Test
        void should_return_null_when_role_is_wrong() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("student01", "pwd123", "ADMIN");

            assertNull(result);
        }

        @Test
        void should_return_null_when_all_credentials_are_wrong() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("nonexistent", "wrong", "WRONGROLE");

            assertNull(result);
        }

        @Test
        void should_return_null_when_username_is_null() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login(null, "pwd123", "STUDENT");

            assertNull(result);
        }

        @Test
        void should_return_null_when_password_is_null() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("student01", null, "STUDENT");

            assertNull(result);
        }

        @Test
        void should_return_null_when_role_is_null() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("student01", "pwd123", null);

            assertNull(result);
        }

        @Test
        void should_return_null_when_all_parameters_are_null() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login(null, null, null);

            assertNull(result);
        }

        @Test
        void should_return_null_when_username_is_empty_string() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("", "pwd123", "STUDENT");

            assertNull(result);
        }

        @Test
        void should_return_null_when_username_is_whitespace_only() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("   ", "pwd123", "STUDENT");

            assertNull(result);
        }

        @Test
        void should_handle_username_with_special_characters() {
            User expected = buildUser(4L, "user@#01", "pwd", "STUDENT");
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(expected);

            User result = userService.login("user@#01", "pwd", "STUDENT");

            assertNotNull(result);
            assertEquals("user@#01", result.getUsername());
        }

        @Test
        void should_handle_username_at_max_length_boundary() {
            String longUsername = "a".repeat(50);
            User expected = buildUser(5L, longUsername, "pwd", "STUDENT");
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(expected);

            User result = userService.login(longUsername, "pwd", "STUDENT");

            assertNotNull(result);
            assertEquals(longUsername, result.getUsername());
        }

        @Test
        void should_handle_role_with_lowercase_when_database_stores_uppercase() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean())).thenReturn(null);

            User result = userService.login("student01", "pwd123", "student");

            assertNull(result);
        }

        @Test
        void should_throw_exception_when_mapper_throws_runtime_exception() {
            when(userMapper.selectOne(any(Wrapper.class), anyBoolean()))
                    .thenThrow(new RuntimeException("Database connection failed"));

            assertThrows(RuntimeException.class,
                    () -> userService.login("student01", "pwd123", "STUDENT"));
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
