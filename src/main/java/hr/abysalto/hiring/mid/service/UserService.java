package hr.abysalto.hiring.mid.service;

import hr.abysalto.hiring.mid.dto.request.RegisterRequest;
import hr.abysalto.hiring.mid.dto.response.UserResponse;
import hr.abysalto.hiring.mid.model.User;

public interface UserService {
    public User register(RegisterRequest request);
    public UserResponse getCurrentUser(String username);
}
