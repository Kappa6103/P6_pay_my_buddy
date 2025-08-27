package com.paymybuddy.service;

import com.paymybuddy.model.AppUser;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public AppUser addUser(AppUser user) {
        return userRepository.save(user);
    }

    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    public Optional<AppUser> getUserById(int id) {
        return userRepository.findById(id);
    }

    public void deleteUser(AppUser user) {
        userRepository.delete(user);
    }

    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") //should i implement role in user class ?
                .build();
    }
}
