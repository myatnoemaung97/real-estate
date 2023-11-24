package com.example.Online.Housing.Show.controllers;

import com.example.Online.Housing.Show.models.Housing;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @GetMapping("/test")
    public ModelAndView test() {
        ModelAndView mav = new ModelAndView("test");
        mav.addObject(new Housing());
        return mav;
    }
}
