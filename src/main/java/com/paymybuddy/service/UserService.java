package com.paymybuddy.service;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public AppUser addUser(AppUser user) {
        return userRepository.save(user);
    }

    public Optional<AppUser> getUserById(int id) {
        return userRepository.findById(id);
    }

    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }

}
