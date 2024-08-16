package com.autopublish.socials.repositories;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findAllByCustomer(Customer customer);
}
