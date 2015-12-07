package com.mephiboys.satia.groovy.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordHash {
    public static void main(String[] args) {
         String password = "mavashik";
         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         String hashedPassword = passwordEncoder.encode(password);
     }
}
