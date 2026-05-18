package com.xian.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xian.medical.entity.User;
import com.xian.medical.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("testpass");
        mockUser.setRole("STUDENT");
    }

    @Test
    void should_return_user_when_username_password_role_are_correct() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(mockUser);

        User result = userService.login("testuser", "testpass", "STUDENT");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("STUDENT", result.getRole());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_username_is_incorrect() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("wronguser", "testpass", "STUDENT");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_password_is_incorrect() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("testuser", "wrongpass", "STUDENT");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_role_is_incorrect() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("testuser", "testpass", "ADMIN");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_username_is_empty() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("", "testpass", "STUDENT");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_password_is_empty() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("testuser", "", "STUDENT");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_role_is_empty() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("testuser", "testpass", "");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_username_is_null() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login(null, "testpass", "STUDENT");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_password_is_null() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("testuser", null, "STUDENT");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_role_is_null() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("testuser", "testpass", null);

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_user_when_role_is_staff() {
        mockUser.setRole("STAFF");
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(mockUser);

        User result = userService.login("testuser", "testpass", "STAFF");

        assertNotNull(result);
        assertEquals("STAFF", result.getRole());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_user_when_role_is_admin() {
        mockUser.setRole("ADMIN");
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(mockUser);

        User result = userService.login("testuser", "testpass", "ADMIN");

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_all_parameters_are_null() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login(null, null, null);

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }

    @Test
    void should_return_null_when_all_parameters_are_empty() {
        when(userMapper.selectOne(any(QueryWrapper.class), eq(true))).thenReturn(null);

        User result = userService.login("", "", "");

        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class), eq(true));
    }
}
