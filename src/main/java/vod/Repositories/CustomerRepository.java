package vod.Repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vod.models.Customer;

import java.util.List;


public interface CustomerRepository extends MongoRepository<Customer, String>
{
    public List<Customer> findByFirstname (String firstName);
    public List<Customer> findByLastname(String lastName);
    public Customer findById(String id);
}
