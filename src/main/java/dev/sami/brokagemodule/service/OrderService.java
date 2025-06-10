package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.*;
import dev.sami.brokagemodule.dto.CreateOrderRequest;
import dev.sami.brokagemodule.exception.InsufficientFundsException;
import dev.sami.brokagemodule.exception.InvalidOrderStatusException;
import dev.sami.brokagemodule.exception.OrderNotFoundException;
import dev.sami.brokagemodule.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final AssetService assetService;
    
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}, asset: {}, side: {}, size: {}, price: {}", 
                request.getCustomerId(), request.getAssetName(), request.getSide(), 
                request.getSize(), request.getPrice());
        
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        customerService.ensureTryAssetExists(request.getCustomerId(), new BigDecimal("100000"));
        
        validateOrderFunds(request);
        
        Order order = new Order(
                request.getCustomerId(),
                request.getAssetName(),
                request.getSide(),
                request.getSize(),
                request.getPrice()
        );
        
        Order savedOrder = orderRepository.save(order);
        
        reserveAssets(request, customer);
        
        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }
    
    public List<Order> getOrdersByCustomerId(Long customerId) {
        log.debug("Getting orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getOrdersByCustomerIdAndDateRange(Long customerId, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        log.debug("Getting orders for customer: {} between {} and {} with status: {}", 
                customerId, startDate, endDate, status);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return orderRepository.findByCustomerIdAndDateRangeAndStatus(customerId, startDateTime, endDateTime, status);
    }
    
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only PENDING orders can be cancelled. Current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
        
        Customer customer = customerService.getCustomerById(order.getCustomerId());
        releaseAssets(order, customer);
        
        log.info("Order {} cancelled successfully", orderId);
    }
    
    public void matchOrder(Long orderId) {
        log.info("Matching order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only PENDING orders can be matched. Current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.MATCHED);
        orderRepository.save(order);
        
        Customer customer = customerService.getCustomerById(order.getCustomerId());
        updateAssetsAfterMatch(order, customer);
        
        log.info("Order {} matched successfully", orderId);
    }
    
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING);
    }
    
    private void validateOrderFunds(CreateOrderRequest request) {
        if (request.getSide() == OrderSide.BUY) {
            BigDecimal totalCost = request.getSize().multiply(request.getPrice());
            if (!customerService.hasEnoughAsset(request.getCustomerId(), "TRY", totalCost)) {
                throw new InsufficientFundsException("Insufficient TRY balance for purchase. Required: " + totalCost);
            }
        } else {
            if (!customerService.hasEnoughAsset(request.getCustomerId(), request.getAssetName(), request.getSize())) {
                throw new InsufficientFundsException("Insufficient " + request.getAssetName() + " balance for sale. Required: " + request.getSize());
            }
        }
    }
    
    private void reserveAssets(CreateOrderRequest request, Customer customer) {
        if (request.getSide() == OrderSide.BUY) {
            BigDecimal totalCost = request.getSize().multiply(request.getPrice());
            assetService.decreaseAssetUsableSize(customer, "TRY", totalCost);
        } else {
            assetService.decreaseAssetUsableSize(customer, request.getAssetName(), request.getSize());
        }
    }
    
    private void releaseAssets(Order order, Customer customer) {
        if (order.getOrderSide() == OrderSide.BUY) {
            BigDecimal totalCost = order.getSize().multiply(order.getPrice());
            assetService.increaseAssetUsableSize(customer, "TRY", totalCost);
        } else {
            assetService.increaseAssetUsableSize(customer, order.getAssetName(), order.getSize());
        }
    }
    
    private void updateAssetsAfterMatch(Order order, Customer customer) {
        if (order.getOrderSide() == OrderSide.BUY) {
            try {
                assetService.increaseAssetUsableSize(customer, order.getAssetName(), order.getSize());
            } catch (Exception e) {
                assetService.createOrUpdateAsset(customer, order.getAssetName(), order.getSize(), order.getSize());
            }
        } else {
            BigDecimal saleProceeds = order.getSize().multiply(order.getPrice());
            assetService.increaseAssetUsableSize(customer, "TRY", saleProceeds);
        }
    }
} 