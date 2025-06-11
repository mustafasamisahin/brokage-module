package dev.sami.brokagemodule.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "assets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "asset_name"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "customer_id")
    private Long customerId;
    
    @NotBlank
    @Column(name = "asset_name", nullable = false)
    private String assetName;
    
    @NotNull
    @Column(nullable = false)
    private BigDecimal size;
    
    @NotNull
    @Column(name = "usable_size", nullable = false)
    private BigDecimal usableSize;
} 