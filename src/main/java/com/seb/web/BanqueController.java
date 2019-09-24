package com.seb.web;

import com.seb.service.IBanqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BanqueController {
    @Autowired
    private IBanqueService banqueService;

    @RequestMapping("/operations")
    public String index(){
        return "comptes";
    }
}
