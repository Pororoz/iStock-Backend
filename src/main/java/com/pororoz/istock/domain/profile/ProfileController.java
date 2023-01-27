package com.pororoz.istock.domain.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final Environment env;

    @GetMapping("profile")
    String profile() {
        String[] activeProfiles = env.getActiveProfiles();
        List<String> springProfiles = Arrays.asList("spring1", "spring2");
        return Arrays.stream(activeProfiles).filter(springProfiles::contains).findAny().orElse("spring1");
    }
}
