package com.example.Online.Housing.Show.models;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationForm {

    private String username;
    private String name;
    private String password;
    private String email;
    private String phone;
    private Long id;

    public Account toAccount(PasswordEncoder passwordEncoder) {
        Account account = new Account();
        account.setName(name);
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setPhone(phone);
        account.setId(id);
        return account;
    }
}