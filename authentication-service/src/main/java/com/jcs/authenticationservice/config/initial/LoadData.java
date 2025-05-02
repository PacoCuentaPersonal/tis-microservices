//package com.jcs.authenticationservice.config.initial;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class LoadData implements CommandLineRunner {
//    private final PasswordEncoder passwordEncoder;
//
//    public LoadData(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        String password= passwordEncoder.encode("tis2025");
//        System.out.println(password);
//    }
//}
