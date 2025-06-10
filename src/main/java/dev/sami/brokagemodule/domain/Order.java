package dev.sami.brokagemodule.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Long customerId;
    
    @NotBlank
    @Column(nullable = false)
    private String assetName;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide orderSide;
    
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal size;
    
    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime createDate;
    
    @PrePersist
    protected void onCreate() {
        if (createDate == null) {
            createDate = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
    
    public Order(Long customerId, String assetName, OrderSide orderSide, BigDecimal size, BigDecimal price) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = OrderStatus.PENDING;
        this.createDate = LocalDateTime.now();
    }
} 