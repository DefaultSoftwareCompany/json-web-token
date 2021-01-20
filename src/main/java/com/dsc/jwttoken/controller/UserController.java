package com.dsc.jwttoken.controller;

import com.dsc.jwttoken.config.JwtTokenProvider;
import com.dsc.jwttoken.model.Role;
import com.dsc.jwttoken.model.User;
import com.dsc.jwttoken.repository.RoleRepository;
import com.dsc.jwttoken.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
public class UserController {
    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UserRepository repository, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider, RoleRepository roleRepository) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/user/save")
    public ResponseEntity<User> save(@RequestBody User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.getByRoleName("USER"));
        user.setRoles(roles);
        return ResponseEntity.ok(repository.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        Map<String, String> map = new HashMap<>();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = repository.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("There is no user with such an username");
        }
        String token = tokenProvider.createToken(username, user.getRoles());
        map.put("username", username);
        map.put("token", token);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/user/get/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        return ResponseEntity.ok(repository.getByUsername(username));
    }
}
