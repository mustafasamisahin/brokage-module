package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Customer;
import dev.sami.brokagemodule.exception.CustomerNotFoundException;
import dev.sami.brokagemodule.exception.DuplicateCustomerException;
import dev.sami.brokagemodule.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        log.debug("Creating new customer: {}", customer);

        if (customerRepository.existsByCustomerId(customer.getCustomerId())) {
            throw new DuplicateCustomerException("Customer with ID " + customer.getCustomerId() + " already exists");
        }

        if (customerRepository.existsByNationalIdentityNumber(customer.getNationalIdentityNumber())) {
            throw new DuplicateCustomerException("Customer with national identity number " +
                    customer.getNationalIdentityNumber() + " already exists");
        }

        return customerRepository.save(customer);
    }

    public Customer getCustomerById(Long customerId) {
        log.debug("Getting customer with ID: {}", customerId);
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + customerId));
    }

    public List<Customer> getAllCustomers() {
        log.debug("Getting all customers");
        return customerRepository.findAll();
    }

    public Customer updateCustomer(Long customerId, Customer updatedCustomer) {
        log.debug("Updating customer with ID: {}", customerId);

        Customer existingCustomer = getCustomerById(customerId);

        if (!existingCustomer.getNationalIdentityNumber().equals(updatedCustomer.getNationalIdentityNumber()) &&
                customerRepository.existsByNationalIdentityNumber(updatedCustomer.getNationalIdentityNumber())) {
            throw new DuplicateCustomerException("National identity number already in use");
        }

        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setSurname(updatedCustomer.getSurname());
        existingCustomer.setNationalIdentityNumber(updatedCustomer.getNationalIdentityNumber());

        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long customerId) {
        log.debug("Deleting customer with ID: {}", customerId);
        if (!customerRepository.existsByCustomerId(customerId)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }
        customerRepository.deleteByCustomerId(customerId);
    }
} 