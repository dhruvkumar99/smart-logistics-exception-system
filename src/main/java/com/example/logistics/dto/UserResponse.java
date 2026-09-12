package com.example.logistics.dto;

import com.example.logistics.entity.User.Role;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
}