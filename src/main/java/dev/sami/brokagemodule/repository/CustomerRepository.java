package dev.sami.brokagemodule.repository;

import dev.sami.brokagemodule.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(Long customerId);
    boolean existsByCustomerId(Long customerId);
    boolean existsByNationalIdentityNumber(String nationalIdentityNumber);
    void deleteByCustomerId(Long customerId);
} 