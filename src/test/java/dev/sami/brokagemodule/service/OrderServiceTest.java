package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.domain.OrderSide;
import dev.sami.brokagemodule.domain.OrderStatus;
import dev.sami.brokagemodule.dto.CreateOrderRequest;
import dev.sami.brokagemodule.exception.InsufficientFundsException;
import dev.sami.brokagemodule.exception.InvalidOrderStatusException;
import dev.sami.brokagemodule.exception.OrderNotFoundException;
import dev.sami.brokagemodule.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private OrderService orderService;

    private CreateOrderRequest buyOrderRequest;
    private CreateOrderRequest sellOrderRequest;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        buyOrderRequest = new CreateOrderRequest();
        buyOrderRequest.setCustomerId(1L);
        buyOrderRequest.setAssetName("AAPL");
        buyOrderRequest.setSide(OrderSide.BUY);
        buyOrderRequest.setSize(new BigDecimal("10"));
        buyOrderRequest.setPrice(new BigDecimal("150.00"));

        sellOrderRequest = new CreateOrderRequest();
        sellOrderRequest.setCustomerId(1L);
        sellOrderRequest.setAssetName("AAPL");
        sellOrderRequest.setSide(OrderSide.SELL);
        sellOrderRequest.setSize(new BigDecimal("5"));
        sellOrderRequest.setPrice(new BigDecimal("155.00"));

        sampleOrder = new Order(1L, "AAPL", OrderSide.BUY, 
                new BigDecimal("10"), new BigDecimal("150.00"));
        sampleOrder.setId(1L);
    }

    @Test
    void createOrder_shouldCreateBuyOrderSuccessfully() {
        when(assetService.hasEnoughUsableSize(eq(1L), eq("TRY"), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Order result = orderService.createOrder(buyOrderRequest);

        assertNotNull(result);
        verify(assetService).ensureTryAssetExists(eq(1L), any(BigDecimal.class));
        verify(assetService).hasEnoughUsableSize(eq(1L), eq("TRY"), any(BigDecimal.class));
        verify(assetService).decreaseAssetUsableSize(eq(1L), eq("TRY"), any(BigDecimal.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowInsufficientFundsForBuyOrder() {
        when(assetService.hasEnoughUsableSize(eq(1L), eq("TRY"), any(BigDecimal.class))).thenReturn(false);

        assertThrows(InsufficientFundsException.class, () -> orderService.createOrder(buyOrderRequest));
    }

    @Test
    void createOrder_shouldCreateSellOrderSuccessfully() {
        when(assetService.hasEnoughUsableSize(eq(1L), eq("AAPL"), any(BigDecimal.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);

        Order result = orderService.createOrder(sellOrderRequest);

        assertNotNull(result);
        verify(assetService).ensureTryAssetExists(eq(1L), any(BigDecimal.class));
        verify(assetService).hasEnoughUsableSize(eq(1L), eq("AAPL"), any(BigDecimal.class));
        verify(assetService).decreaseAssetUsableSize(eq(1L), eq("AAPL"), any(BigDecimal.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void cancelOrder_shouldCancelPendingOrderSuccessfully() {
        sampleOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELED, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void cancelOrder_shouldThrowExceptionForNonPendingOrder() {
        sampleOrder.setStatus(OrderStatus.MATCHED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void cancelOrder_shouldThrowExceptionForNonExistentOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void matchOrder_shouldMatchPendingOrderSuccessfully() {
        sampleOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        orderService.matchOrder(1L);

        assertEquals(OrderStatus.MATCHED, sampleOrder.getStatus());
        verify(orderRepository).save(sampleOrder);
    }

    @Test
    void getPendingOrders_shouldReturnPendingOrders() {
        List<Order> pendingOrders = List.of(sampleOrder);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        List<Order> result = orderService.getPendingOrders();

        assertEquals(1, result.size());
        assertEquals(sampleOrder, result.get(0));
    }

    @Test
    void matchOrder_shouldThrowExceptionForNonPendingOrder() {
        sampleOrder.setStatus(OrderStatus.MATCHED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.matchOrder(1L));
    }
} 