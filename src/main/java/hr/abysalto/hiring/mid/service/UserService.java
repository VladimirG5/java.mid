package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import hr.abysalto.hiring.mid.dto.response.UserResponse;
import hr.abysalto.hiring.mid.model.User;

public interface UserService {
    User register(RegisterRequest request);

    User getUser(String username);

    UserResponse getCurrentUser(String username);
}
