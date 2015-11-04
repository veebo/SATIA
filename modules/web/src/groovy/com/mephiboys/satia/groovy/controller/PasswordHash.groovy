package com.mephiboys.satia.groovy.controller

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class PasswordHash {
     def static main(){
         String password = 'EnoughSecure';
         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         String hashedPassword = passwordEncoder.encode(password);
     }
}
