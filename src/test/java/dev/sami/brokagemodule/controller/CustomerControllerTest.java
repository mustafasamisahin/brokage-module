package dev.sami.brokagemodule.controller;

import dev.sami.brokagemodule.domain.Customer;
import dev.sami.brokagemodule.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void createCustomerTest() {
        Customer inputCustomer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");

        when(customerService.createCustomer(inputCustomer)).thenReturn(inputCustomer);

        ResponseEntity<Customer> expectedResponse = new ResponseEntity<>(inputCustomer, HttpStatus.CREATED);
        ResponseEntity<Customer> actualResponse = customerController.createCustomer(inputCustomer);

        verify(customerService, times(1)).createCustomer(inputCustomer);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getCustomerTest() {
        Customer customer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        Long customerId = 1L;

        when(customerService.getCustomerById(customerId)).thenReturn(customer);
        ResponseEntity<Customer> expectedResponse = new ResponseEntity<>(customer, HttpStatus.OK);
        ResponseEntity<Customer> actualResponse = customerController.getCustomer(customerId);
        verify(customerService, times(1)).getCustomerById(customerId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAllCustomersTest() {
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        Customer customer2 = new Customer(2L, "Mustafa", "Sahin", "123", "Istanbul");
        customers.add(customer1);
        customers.add(customer2);

        when(customerService.getAllCustomers()).thenReturn(customers);
        ResponseEntity<List<Customer>> expectedResponse = new ResponseEntity<>(customers, HttpStatus.OK);
        ResponseEntity<List<Customer>> actualResponse = customerController.getAllCustomers();
        verify(customerService, times(1)).getAllCustomers();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateCustomerTest() {
        Long customerId = 1L;
        Customer updatedCustomer = new Customer(1L, "MustafaSami", "Sahin", "111", "Istanbul/Umraniye");

        when(customerService.updateCustomer(customerId, updatedCustomer)).thenReturn(updatedCustomer);
        ResponseEntity<Customer> expectedResponse = new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        ResponseEntity<Customer> actualResponse = customerController.updateCustomer(customerId, updatedCustomer);
        verify(customerService, times(1)).updateCustomer(customerId, updatedCustomer);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteCustomerTest() {
        Long customerId = 1L;

        customerController.deleteCustomer(customerId);
        verify(customerService, times(1)).deleteCustomer(customerId);
    }

}