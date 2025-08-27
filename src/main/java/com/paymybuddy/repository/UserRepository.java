package com.paymybuddy.repository;

import com.paymybuddy.model.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Integer> {

    public AppUser findByEmail(String email);

}
