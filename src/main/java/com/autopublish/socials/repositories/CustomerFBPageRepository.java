package com.autopublish.socials.repositories;

import com.autopublish.socials.entities.Customer;
import com.autopublish.socials.entities.CustomerFBPage;
import com.autopublish.socials.entities.CustomerPageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerFBPageRepository extends JpaRepository<CustomerFBPage, CustomerPageId> {

    public List<CustomerFBPage> findByCustomer(Customer customer);
    public List<CustomerFBPage> findByCustomerAndPublishEnabled(Customer customer,
                                                                boolean publishEnabled);
}
