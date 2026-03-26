package hr.abysalto.hiring.mid.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.abysalto.hiring.mid.dto.request.LoginRequest;
import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void register_whenRequestIsValid_thenReturnTokenAndUsername() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("auth_register_valid")
                .email("auth_register_valid@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("auth_register_valid"));
    }

    @Test
    void register_whenUsernameAlreadyTaken_thenReturn409() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("auth_duplicate_user")
                .email("auth_duplicate_user@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        RegisterRequest duplicate = RegisterRequest.builder()
                .username("auth_duplicate_user")
                .email("auth_duplicate_user2@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_whenEmailAlreadyInUse_thenReturn409() throws Exception {
        RegisterRequest first = RegisterRequest.builder()
                .username("auth_email_user1")
                .email("auth_shared_email@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        RegisterRequest second = RegisterRequest.builder()
                .username("auth_email_user2")
                .email("auth_shared_email@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_whenUsernameIsBlank_thenReturn400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .username("")
                .email("auth_blank_username@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_whenCredentialsAreValid_thenReturnToken() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("auth_login_user")
                .email("auth_login_user@example.com")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = LoginRequest.builder()
                .username("auth_login_user")
                .password("secret123")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("auth_login_user"));
    }

    @Test
    void login_whenCredentialsAreInvalid_thenReturn401() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("nobody")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
