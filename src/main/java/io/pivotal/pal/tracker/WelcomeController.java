package io.pivotal.pal.tracker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by accenturelabs on 3/26/18.
 */

@RestController
public class WelcomeController {

    @GetMapping
    public String sayHello() {
        return "hello, George";
    }
}
