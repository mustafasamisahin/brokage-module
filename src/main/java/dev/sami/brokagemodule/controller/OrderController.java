package dev.sami.brokagemodule.controller;

import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.domain.OrderStatus;
import dev.sami.brokagemodule.dto.CreateOrderRequest;
import dev.sami.brokagemodule.dto.OrderResponse;
import dev.sami.brokagemodule.mapper.OrderMapper;
import dev.sami.brokagemodule.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order request received: {}", request);
        Order order = orderService.createOrder(request);
        OrderResponse response = orderMapper.toOrderResponse(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) OrderStatus status) {
        
        log.info("Getting orders for customer: {}, startDate: {}, endDate: {}, status: {}", 
                customerId, startDate, endDate, status);
        
        List<Order> orders;
        if (startDate != null && endDate != null) {
            orders = orderService.getOrdersByCustomerIdAndDateRange(customerId, startDate, endDate, status);
        } else {
            orders = orderService.getOrdersByCustomerId(customerId);
        }
        
        List<OrderResponse> responses = orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        log.info("Cancel order request received for order: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{orderId}/match")
    public ResponseEntity<Void> matchOrder(@PathVariable Long orderId) {
        log.info("Match order request received for order: {}", orderId);
        orderService.matchOrder(orderId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrders() {
        log.info("Getting all pending orders");
        List<Order> orders = orderService.getPendingOrders();
        List<OrderResponse> responses = orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

} 