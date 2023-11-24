package com.example.Online.Housing.Show.controllers;

import com.example.Online.Housing.Show.models.Account;
import com.example.Online.Housing.Show.models.RegistrationForm;
import com.example.Online.Housing.Show.repositories.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class RegistrationController {

    private final AccountRepository accRepo;
    private final PasswordEncoder passwordEncoder;


    public RegistrationController(
            AccountRepository accRepo, PasswordEncoder passwordEncoder) {
        this.accRepo = accRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public ModelAndView registerForm() {
        ModelAndView mav = new ModelAndView("registration");
        mav.addObject(new Account());
        return mav;
    }

    @PostMapping("/register")
    public String processRegistration(RegistrationForm form) {
        Account savedAccount = accRepo.save(form.toAccount(passwordEncoder));
        Long accountId = savedAccount.getId();
        return "redirect:/account/" + accountId + "/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }



}
