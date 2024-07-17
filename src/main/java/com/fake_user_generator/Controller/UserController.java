package com.fake_user_generator.Controller;


import com.fake_user_generator.Entities.User;
import com.fake_user_generator.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/generate")
    public String generateUsers(@RequestParam int count,
                                @RequestParam String region,
                                @RequestParam int errorPerRecord,
                                @RequestParam long seed,
                                RedirectAttributes redirectAttributes) {
        List<User> users = userService.generateFakeUsers(count, region, errorPerRecord, seed);
        redirectAttributes.addFlashAttribute("users", users);
        return "redirect:/users/view";
    }

    @GetMapping("/view")
    public String viewUsers(Model model) {
        if (!model.containsAttribute("users")) {
            model.addAttribute("users", new ArrayList<User>());
        }
        return "index";
    }
}