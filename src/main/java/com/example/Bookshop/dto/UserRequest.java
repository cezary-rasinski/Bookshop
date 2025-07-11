package com.example.Bookshop.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String login;
    private String password;
}
