package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by accenturelabs on 3/26/18.
 */

@RestController
public class WelcomeController {
    private String message;


    public WelcomeController(@Value("${WELCOME_MESSAGE:NOT SET}") String message) {
        this.message = message;
    }

    @GetMapping("/")
    public String sayHello() {
        return message;
    }
}
