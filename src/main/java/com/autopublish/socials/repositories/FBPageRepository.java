package com.autopublish.socials.repositories;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.FBPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FBPageRepository extends JpaRepository<FBPage, String> {

//    public List<FBPage> findByCustomers(Set<Customer> customer);
    public Optional<FBPage> findByPageId(String id);
}
