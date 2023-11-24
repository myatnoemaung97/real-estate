package com.example.Online.Housing.Show.controllers;

import com.example.Online.Housing.Show.models.Account;
import com.example.Online.Housing.Show.models.Housing;
import com.example.Online.Housing.Show.models.HousingWithOwnerInfo;
import com.example.Online.Housing.Show.repositories.AccountRepository;
import com.example.Online.Housing.Show.repositories.HousingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final HousingRepository houseRepo;
    private final AccountRepository accRepo;
    private final ModelMapper mapper;

    public HomeController(HousingRepository houseRepo, AccountRepository accRepo, ModelMapper mapper) {
        this.houseRepo = houseRepo;
        this.accRepo = accRepo;
        this.mapper = mapper;
    }

    @GetMapping({"/","/home",""})
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        List<Housing> housings = houseRepo.findAll();
        List<HousingWithOwnerInfo> housingCards = new ArrayList<>();
        for (Housing housing: housings) {
            HousingWithOwnerInfo housingWithOwnerInfo = mapper.map(housing, HousingWithOwnerInfo.class);
            Account owner = accRepo.findById(housing.getOwnerId()).get();
            housingWithOwnerInfo.setOwnerName(owner.getName());
            housingWithOwnerInfo.setOwnerEmail(owner.getEmail());
            housingWithOwnerInfo.setOwnerPhone(owner.getPhone());
            housingCards.add(housingWithOwnerInfo);
        }
        mav.addObject("housingCards", housingCards);
        return mav;
    }
}
