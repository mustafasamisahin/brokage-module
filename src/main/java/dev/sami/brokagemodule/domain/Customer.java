package dev.sami.brokagemodule.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @Column(name = "customer_id")
    private Long customerId;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotBlank
    @Column(nullable = false)
    private String surname;
    
    @NotBlank
    @Column(name = "national_identity_number", nullable = false, unique = true)
    private String nationalIdentityNumber;

    @Column(name = "address")
    private String address;
} 