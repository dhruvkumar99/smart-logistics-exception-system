package com.example.logistics.service;

import com.example.logistics.dto.UserRequest;
import com.example.logistics.dto.UserResponse;
import com.example.logistics.entity.User;
import com.example.logistics.exception.BusinessException;
import com.example.logistics.exception.ResourceNotFoundException;
import com.example.logistics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword()) // hash in production
                .role(request.getRole())
                .active(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public UserResponse update(Long id, UserRequest request) {
        User user = findEntity(id);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        return toResponse(userRepository.save(user));
    }

    public void toggleActive(Long id) {
        User user = findEntity(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.delete(findEntity(id));
    }

    public User findEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId()).name(u.getName()).email(u.getEmail())
                .role(u.getRole()).active(u.isActive()).build();
    }
}