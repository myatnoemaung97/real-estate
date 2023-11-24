package com.example.Online.Housing.Show.controllers;

import com.example.Online.Housing.Show.models.Account;
import com.example.Online.Housing.Show.models.Housing;
import com.example.Online.Housing.Show.repositories.AccountRepository;
import com.example.Online.Housing.Show.repositories.HousingRepository;
import com.example.Online.Housing.Show.services.AccountService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AccountController {

    private final AccountRepository accRepo;
    private final HousingRepository houseRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public AccountController(AccountRepository accRepo, HousingRepository houseRepo, PasswordEncoder passwordEncoder, AccountService accountService) {
        this.accRepo = accRepo;
        this.houseRepo = houseRepo;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    @GetMapping("/account/{accountId}/home")
    public ModelAndView accountHomePage(
            HttpServletResponse response,
            @PathVariable Long accountId
            ) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        ModelAndView modelAndView = new ModelAndView("account-home-page");

        Account account = accRepo.findById(accountId).get();
        modelAndView.addObject("account", account);
        List<Housing> ownHousings = houseRepo.findByOwnerId(account.getId());
        modelAndView.addObject("ownHousings", ownHousings);
        return modelAndView;
    }

    @GetMapping("/account/{id}/editAccount")
    public ModelAndView editAccount(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("contact-edit");
        Account account = accRepo.findById(id).get();
        System.out.println(account.getPassword());
        mav.addObject("account", account);
        return mav;
    }

    @PostMapping("/account/{id}/updateAccount")
    public String updateAccount(@PathVariable Long id, @ModelAttribute Account account) {
        accRepo.updateAccount(account.getName(), account.getEmail(), account.getPhone(), id);
        return "redirect:/account/{id}/home";
    }

}

