package vod.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vod.Repositories.CustomerRepository;
import vod.models.Customer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    @Autowired
    CustomerRepository repo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Customer> getAllCustomers(@RequestParam Map<String, String> params) {
        return repo.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Customer> getCustomerById(@PathVariable("id") String id) {
        Customer c = repo.findById(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(id).toUri());
        HttpStatus status;
        if(c != null)
            status = HttpStatus.OK;
        else
            status = HttpStatus.NOT_FOUND;
            return new ResponseEntity<Customer>(c,httpHeaders,status);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    ResponseEntity<?> addCustomer(@RequestBody Customer input) {
        repo.save(input);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(input.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updateCustomer(@PathVariable("id") String id, @RequestBody Customer input) {
        Customer c = validateCustomer(id);
        c.setFirstname(input.getFirstname());
        c.setLastname(input.getLastname());
        repo.save(c);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/").buildAndExpand(input.getId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.ACCEPTED);
    }

    private Customer validateCustomer(String id)
    {
        Customer c = this.repo.findById(id);
        if(c == null)
            throw new CustomerNotFoundException(id);
        return c;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    class CustomerNotFoundException extends RuntimeException
    {
        public CustomerNotFoundException(String id)
        {
            super("Could not find customer id: " + id + ".");
        }
    }
}
