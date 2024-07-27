package com.fake_user_generator.Controller;

import com.fake_user_generator.Entities.User;
import com.fake_user_generator.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/generate")
    public ResponseEntity<List<User>> generateUsers(@RequestParam int count,
                                                    @RequestParam String region,
                                                    @RequestParam int errorPerRecord,
                                                    @RequestParam long seed,
                                                    @RequestParam int page) {
        List<User> users = userService.generateFakeUsers(count, region, errorPerRecord, seed, page);
        return ResponseEntity.ok(users);
    }
}
