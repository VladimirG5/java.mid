package hr.abysalto.hiring.mid.util;

import hr.abysalto.hiring.mid.dto.request.LoginRequest;
import hr.abysalto.hiring.mid.dto.request.RegisterRequest;

public class DataGenerator {

    // RegisterRequest

    public static RegisterRequest.RegisterRequestBuilder requestRegisterWithDefaultValues() {
        return RegisterRequest.builder()
                .username("john")
                .password("secret123")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe");
    }

    public static RegisterRequest.RegisterRequestBuilder requestRegisterWithoutUsername() {
        return requestRegisterWithDefaultValues().username("");
    }

    public static RegisterRequest.RegisterRequestBuilder requestRegisterWithoutEmail() {
        return requestRegisterWithDefaultValues().email("");
    }

    public static RegisterRequest.RegisterRequestBuilder requestRegisterWithoutPassword() {
        return requestRegisterWithDefaultValues().password("");
    }

    // Login Request

    public static LoginRequest.LoginRequestBuilder requestLoginWithDefaultValues() {
        return LoginRequest.builder()
                .username("john")
                .password("secret123");
    }

    public static LoginRequest.LoginRequestBuilder requestLoginWithoutPassword() {
        return LoginRequest.builder()
                .password("");
    }
}
