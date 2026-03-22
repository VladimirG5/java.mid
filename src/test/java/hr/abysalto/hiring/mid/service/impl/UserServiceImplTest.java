package hr.abysalto.hiring.mid.service.impl;

import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import hr.abysalto.hiring.mid.dto.response.UserResponse;
import hr.abysalto.hiring.mid.model.User;
import hr.abysalto.hiring.mid.repository.UserRepository;
import hr.abysalto.hiring.mid.util.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void register_whenRequestIsValid_thenRegisterTheUser() {
        RegisterRequest request = DataGenerator.requestRegisterWithDefaultValues().build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        User saved = User.builder().id(1L).username("john").email("john@example.com")
                .password("hashed").firstName("John").lastName("Doe").build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.register(request);

        assertThat(result.getUsername()).isEqualTo("john");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_whenUsernameIsTaken_thenThrowException() {
        RegisterRequest request = DataGenerator.requestRegisterWithDefaultValues().build();

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already taken");
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenEmailIsTaken_thenThrowException() {
        RegisterRequest request = DataGenerator.requestRegisterWithDefaultValues().build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getCurrentUser_whenRequestIsValid_thenReturnUser() {
        User user = User.builder().id(1L).username("john").email("john@example.com")
                .firstName("John").lastName("Doe").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserResponse response = userService.getCurrentUser("john");

        assertThat(response.getUsername()).isEqualTo("john");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getFirstName()).isEqualTo("John");
    }

    @Test
    void getCurrentUser_whenUserIsNotFound_thenThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser("ghost"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}