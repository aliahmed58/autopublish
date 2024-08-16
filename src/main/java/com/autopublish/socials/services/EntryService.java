package com.autopublish.socials.services;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.Entry;
import com.autopublish.socials.repositories.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {
    @Autowired
    private EntryRepository entryRepository;

    public void save(Entry entry) {
        entryRepository.save(entry);
    }

    public List<Entry> findAll() {
        return entryRepository.findAll();
    }

    public List<Entry> findAllByCustomer(Customer customer) {
        return entryRepository.findAllByCustomer(customer);
    }
}
