package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.domain.OrderSide;
import dev.sami.brokagemodule.domain.OrderStatus;
import dev.sami.brokagemodule.dto.CreateOrderRequest;
import dev.sami.brokagemodule.exception.*;
import dev.sami.brokagemodule.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final CustomerService customerService;

    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}, asset: {}, side: {}, size: {}, price: {}",
                request.getCustomerId(), request.getAssetName(), request.getSide(),
                request.getSize(), request.getPrice());

        validateOrder(request);

        Order order = new Order(
                request.getCustomerId(),
                request.getAssetName(),
                request.getSide(),
                request.getSize(),
                request.getPrice()
        );

        Order savedOrder = orderRepository.save(order);

        reserveAssets(request, order.getCustomerId());

        log.info("Order created successfully with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        log.debug("Getting orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId);
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

        releaseAssets(order, order.getCustomerId());

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

        updateAssetsAfterMatch(order, order.getCustomerId());

        log.info("Order {} matched successfully", orderId);
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private void validateOrder(CreateOrderRequest request) {
        if (customerService.getCustomerById(request.getCustomerId()) == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        if (request.getSide() == OrderSide.BUY) {
            if (assetService.getAssetByCustomerAndName(request.getCustomerId(), request.getAssetName()) == null) {
                assetService.createOrUpdateAsset(request.getCustomerId(), request.getAssetName(), BigDecimal.ZERO, BigDecimal.ZERO);
            }
            BigDecimal totalCost = request.getSize().multiply(request.getPrice());
            if (assetService.isAssetInsufficient(request.getCustomerId(), "TRY", totalCost)) {
                throw new InsufficientFundsException("Insufficient TRY balance for purchase. Required: " + totalCost);
            }
        } else {
            if ("TRY".equals(request.getAssetName())) {
                throw new IllegalArgumentException("TRY asset cant be sold");
            }

            if (assetService.isAssetInsufficient(request.getCustomerId(), request.getAssetName(), request.getSize())) {
                throw new InsufficientFundsException("Insufficient " + request.getAssetName() + " balance for sale. Required: " + request.getSize());
            }
        }
    }

    private void reserveAssets(CreateOrderRequest request, Long customerId) {
        if (request.getSide() == OrderSide.BUY) {
            BigDecimal totalCost = request.getSize().multiply(request.getPrice());
            assetService.decreaseAssetUsableSize(customerId, "TRY", totalCost);
        } else {
            assetService.decreaseAssetUsableSize(customerId, request.getAssetName(), request.getSize());
        }
    }

    private void releaseAssets(Order order, Long customerId) {
        if (order.getOrderSide() == OrderSide.BUY) {
            BigDecimal totalCost = order.getSize().multiply(order.getPrice());
            assetService.increaseAssetUsableSize(customerId, "TRY", totalCost);
        } else {
            assetService.increaseAssetUsableSize(customerId, order.getAssetName(), order.getSize());
        }
    }

    private void updateAssetsAfterMatch(Order order, Long customerId) {
        if (order.getOrderSide() == OrderSide.BUY) {
            try {
                BigDecimal totalCost = order.getSize().multiply(order.getPrice());
                assetService.increaseAssetSizeAndUsableSize(customerId, order.getAssetName(), order.getSize());
                assetService.decreaseAssetSize(customerId, "TRY", totalCost);
            } catch (Exception e) {
                assetService.createOrUpdateAsset(customerId, order.getAssetName(), order.getSize(), order.getSize());
            }
        } else {
            BigDecimal saleProceeds = order.getSize().multiply(order.getPrice());
            assetService.increaseAssetSizeAndUsableSize(customerId, "TRY", saleProceeds);
            assetService.decreaseAssetSize(customerId, order.getAssetName(), order.getSize());
        }
    }
} 