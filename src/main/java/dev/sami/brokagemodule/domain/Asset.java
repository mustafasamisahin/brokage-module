package dev.sami.brokagemodule.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "assets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "asset_name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @NotBlank
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @NotNull
    @Column(nullable = false)
    private BigDecimal size;
    
    @NotNull
    @Column(name = "usable_size", nullable = false)
    private BigDecimal usableSize;
    
    public Asset(Customer customer, String assetName, BigDecimal size, BigDecimal usableSize) {
        this.customer = customer;
        this.assetName = assetName;
        this.size = size;
        this.usableSize = usableSize;
    }
} 