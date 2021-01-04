package com.depromeet.team5.integration;

import com.depromeet.team5.Team5InterfacesApplication;
import com.depromeet.team5.domain.user.SocialTypes;
import com.depromeet.team5.domain.user.User;
import com.depromeet.team5.dto.LoginResponse;
import com.depromeet.team5.dto.UserDto;
import com.depromeet.team5.dto.UserResponse;
import com.depromeet.team5.integration.api.UserTestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(classes = Team5InterfacesApplication.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class UserTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private UserTestController userTestController;

    @BeforeEach
    void setUp() {
        userTestController = new UserTestController(mockMvc, objectMapper);
    }

    @Test
    void getMe() throws Exception {
        // given
        LoginResponse loginDto = userTestController.createTestUser();
        String accessToken = loginDto.getToken();
        userTestController.setNickname(accessToken, loginDto.getUserId(), "nickname");
        // when
        UserResponse userResponse = userTestController.getMe(accessToken);
        // then
        assertThat(userResponse.getUserId()).isEqualTo(loginDto.getUserId());
        assertThat(userResponse.getName()).isEqualTo("nickname");
    }

    @Test
    void setNicknameTest() throws Exception {
        // given
        UserDto userDto = new UserDto();
        userDto.setSocialType(SocialTypes.KAKAO);
        userDto.setSocialId("socialId");
        LoginResponse loginDto = userTestController.login(userDto);
        String accessToken = loginDto.getToken();
        Long userId = loginDto.getUserId();
        // when
        userTestController.setNickname(accessToken, userId, "nickname");
        // then
        User user = userTestController.userInfo(accessToken, userId);
        assertThat(user.getName()).isEqualTo("nickname");
    }
}
