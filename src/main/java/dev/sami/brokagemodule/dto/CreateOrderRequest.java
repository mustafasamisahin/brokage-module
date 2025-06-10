package dev.sami.brokagemodule.dto;

import dev.sami.brokagemodule.domain.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotBlank(message = "Asset name is required")
    private String assetName;
    
    @NotNull(message = "Order side is required")
    private OrderSide side;
    
    @NotNull(message = "Size is required")
    @DecimalMin(value = "0.01", message = "Size must be greater than 0")
    private BigDecimal size;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
} 