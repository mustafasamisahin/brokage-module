package dev.sami.brokagemodule.mapper;

import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.domain.OrderSide;
import dev.sami.brokagemodule.domain.OrderStatus;
import dev.sami.brokagemodule.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;
    private Order order;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        
        order = new Order(
                1L, "AAPL", OrderSide.BUY, new BigDecimal("10"), new BigDecimal("150.00")
        );
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.of(2024, 1, 15, 10, 30));
    }

    @Test
    void toOrderResponse_shouldMapOrderToOrderResponse() {
        Order order = new Order(1L, "AAPL", OrderSide.BUY, 
                new BigDecimal("10"), new BigDecimal("150.50"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        OrderResponse response = orderMapper.toOrderResponse(order);

        assertNotNull(response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getCustomerId(), response.getCustomerId());
        assertEquals(order.getAssetName(), response.getAssetName());
        assertEquals(order.getOrderSide(), response.getOrderSide());
        assertEquals(order.getSize(), response.getSize());
        assertEquals(order.getPrice(), response.getPrice());
        assertEquals(order.getStatus(), response.getStatus());
        assertEquals(order.getCreateDate(), response.getCreateDate());
    }

    @Test
    void toOrderResponse_shouldReturnNullForNullOrder() {
        Order order = null;

        OrderResponse response = orderMapper.toOrderResponse(order);

        assertNull(response);
    }

    @Test
    void toOrderResponse_shouldHandleAllOrderSideValues() {
        Order buyOrder = new Order(1L, "AAPL", OrderSide.BUY, 
                new BigDecimal("10"), new BigDecimal("150.50"));
        Order sellOrder = new Order(2L, "AAPL", OrderSide.SELL, 
                new BigDecimal("5"), new BigDecimal("155.00"));

        OrderResponse buyResponse = orderMapper.toOrderResponse(buyOrder);
        OrderResponse sellResponse = orderMapper.toOrderResponse(sellOrder);

        assertEquals(OrderSide.BUY, buyResponse.getOrderSide());
        assertEquals(OrderSide.SELL, sellResponse.getOrderSide());
    }

    @Test
    void toOrderResponse_WithSellOrder_ShouldMapCorrectly() {
        // Given
        Order sellOrder = new Order(
                2L, "TSLA", OrderSide.SELL, new BigDecimal("5"), new BigDecimal("200.00")
        );
        sellOrder.setId(2L);
        sellOrder.setStatus(OrderStatus.MATCHED);
        sellOrder.setCreateDate(LocalDateTime.of(2024, 1, 16, 14, 45));

        // When
        OrderResponse response = orderMapper.toOrderResponse(sellOrder);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(2L, response.getCustomerId());
        assertEquals("TSLA", response.getAssetName());
        assertEquals(OrderSide.SELL, response.getOrderSide());
        assertEquals(new BigDecimal("5"), response.getSize());
        assertEquals(new BigDecimal("200.00"), response.getPrice());
        assertEquals(OrderStatus.MATCHED, response.getStatus());
        assertEquals(LocalDateTime.of(2024, 1, 16, 14, 45), response.getCreateDate());
    }
} 