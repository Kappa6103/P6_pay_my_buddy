package com.paymybuddy.repository;

import com.paymybuddy.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Integer> {

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUserName(String userName);

}
