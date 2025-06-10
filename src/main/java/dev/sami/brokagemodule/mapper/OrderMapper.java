package dev.sami.brokagemodule.mapper;

import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.dto.OrderResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    
    public OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide(),
                order.getSize(),
                order.getPrice(),
                order.getStatus(),
                order.getCreateDate()
        );
    }
} 