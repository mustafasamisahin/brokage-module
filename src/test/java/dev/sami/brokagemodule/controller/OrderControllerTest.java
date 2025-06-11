package dev.sami.brokagemodule.controller;

import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.domain.OrderSide;
import dev.sami.brokagemodule.domain.OrderStatus;
import dev.sami.brokagemodule.dto.CreateOrderRequest;
import dev.sami.brokagemodule.dto.OrderResponse;
import dev.sami.brokagemodule.mapper.OrderMapper;
import dev.sami.brokagemodule.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private Order order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order(1L, "BTC", OrderSide.BUY, new BigDecimal("2.5"), new BigDecimal("50000"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        orderResponse = OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .assetName(order.getAssetName())
                .orderSide(order.getOrderSide())
                .size(order.getSize())
                .price(order.getPrice())
                .status(order.getStatus())
                .createDate(order.getCreateDate())
                .build();
    }

    @Test
    void testCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide(),
                order.getSize(),
                order.getPrice()
        );

        when(orderService.createOrder(request)).thenReturn(order);
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        ResponseEntity<OrderResponse> response = orderController.createOrder(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).createOrder(request);
    }

    @Test
    void testGetOrdersByCustomer() {
        when(orderService.getOrdersByCustomerId(1L)).thenReturn(List.of(order));
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        ResponseEntity<List<OrderResponse>> response = orderController.getOrdersByCustomer(1L, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(orderResponse, response.getBody().get(0));
        verify(orderService).getOrdersByCustomerId(1L);
    }

    @Test
    void testCancelOrder() {
        ResponseEntity<Void> response = orderController.cancelOrder(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(orderService).cancelOrder(1L);
    }

    @Test
    void testMatchOrder() {
        ResponseEntity<Void> response = orderController.matchOrder(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(orderService).matchOrder(1L);
    }

    @Test
    void testGetPendingOrders() {
        when(orderService.getPendingOrders()).thenReturn(List.of(order));
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        ResponseEntity<List<OrderResponse>> response = orderController.getPendingOrders();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(orderService).getPendingOrders();
    }

    @Test
    void testGetAllOrders() {
        when(orderService.getAllOrders()).thenReturn(List.of(order));
        when(orderMapper.toOrderResponse(order)).thenReturn(orderResponse);

        ResponseEntity<List<OrderResponse>> response = orderController.getAllOrders();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(orderService).getAllOrders();
    }
}
