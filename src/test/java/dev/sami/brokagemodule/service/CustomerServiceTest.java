package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Customer;
import dev.sami.brokagemodule.exception.CustomerNotFoundException;
import dev.sami.brokagemodule.exception.DuplicateCustomerException;
import dev.sami.brokagemodule.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomerTest() {
        Customer inputCustomer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        when(customerRepository.existsByCustomerId(1L)).thenReturn(false);
        when(customerRepository.existsByNationalIdentityNumber(inputCustomer.getNationalIdentityNumber())).thenReturn(false);
        when(customerRepository.save(inputCustomer)).thenReturn(inputCustomer);

        Customer actualCustomer = customerService.createCustomer(inputCustomer);

        verify(customerRepository, times(1)).save(inputCustomer);
        assertEquals(inputCustomer, actualCustomer);
    }

    @Test
    void getCustomerByIdTest() {
        Long customerId = 1L;
        Customer expectedCustomer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");

        when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(expectedCustomer));
        Customer actualCustomer = customerService.getCustomerById(customerId);
        verify(customerRepository, times(1)).findByCustomerId(customerId);
        assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void getAllCustomersTest() {
        List<Customer> expectedCustomers = Arrays.asList(
                new Customer(1L, "Sami", "Sahin", "111", "Istanbul"),
                new Customer(2L, "Mustafa", "Sahin", "222", "Ankara")
        );

        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        List<Customer> actualCustomers = customerService.getAllCustomers();

        verify(customerRepository, times(1)).findAll();
        assertEquals(expectedCustomers, actualCustomers);
    }

    @Test
    void updateCustomerTest() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        Customer updatedCustomer = new Customer(1L, "Sami", "Sahin", "222", "Istanbul");

        when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByNationalIdentityNumber("222")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = customerService.updateCustomer(customerId, updatedCustomer);

        verify(customerRepository, times(1)).save(existingCustomer);
        assertEquals("222", result.getNationalIdentityNumber());
    }

    @Test
    void deleteCustomerTest() {
        Long customerId = 1L;
        when(customerRepository.existsByCustomerId(customerId)).thenReturn(true);

        customerService.deleteCustomer(customerId);

        verify(customerRepository, times(1)).deleteByCustomerId(customerId);
    }

    // Additional edge case tests (optional but good practice)

    @Test
    void createCustomer_throwsDuplicateCustomerIdException() {
        Customer customer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        when(customerRepository.existsByCustomerId(1L)).thenReturn(true);

        assertThrows(DuplicateCustomerException.class, () -> customerService.createCustomer(customer));
    }

    @Test
    void createCustomer_throwsDuplicateNationalIdException() {
        Customer customer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        when(customerRepository.existsByCustomerId(1L)).thenReturn(false);
        when(customerRepository.existsByNationalIdentityNumber("111")).thenReturn(true);

        assertThrows(DuplicateCustomerException.class, () -> customerService.createCustomer(customer));
    }

    @Test
    void getCustomerById_throwsCustomerNotFoundException() {
        when(customerRepository.findByCustomerId(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(99L));
    }

    @Test
    void updateCustomer_throwsDuplicateNationalIdException() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        Customer updatedCustomer = new Customer(1L, "Sami", "Sahin", "222", "Istanbul");

        when(customerRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByNationalIdentityNumber("222")).thenReturn(true);

        assertThrows(DuplicateCustomerException.class, () -> customerService.updateCustomer(customerId, updatedCustomer));
    }

    @Test
    void deleteCustomer_throwsCustomerNotFoundException() {
        Long customerId = 1L;
        when(customerRepository.existsByCustomerId(customerId)).thenReturn(false);

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(customerId));
    }
}