package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.*;
import dev.sami.brokagemodule.dto.CreateOrderRequest;
import dev.sami.brokagemodule.exception.*;
import dev.sami.brokagemodule.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreateOrderRequest buildBuyOrderRequest() {
        return new CreateOrderRequest(1L, "BTC", OrderSide.BUY, new BigDecimal("2.0"), new BigDecimal("100.00"));
    }

    @Test
    void shouldThrowIfCustomerNotFound() {
        CreateOrderRequest request = buildBuyOrderRequest();
        when(customerService.getCustomerById(1L)).thenReturn(null);

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        Order order = new Order(1L, "BTC", OrderSide.SELL, new BigDecimal("1"), new BigDecimal("1000"));
        order.setId(42L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(42L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(assetService).increaseAssetUsableSize(1L, "BTC", new BigDecimal("1"));
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowIfCancelOrderIsNotPending() {
        Order order = new Order(1L, "BTC", OrderSide.SELL, new BigDecimal("1"), new BigDecimal("1000"));
        order.setId(42L);
        order.setStatus(OrderStatus.MATCHED);

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(42L))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    void shouldMatchBuyOrderSuccessfully() {
        Order order = new Order(1L, "BTC", OrderSide.BUY, new BigDecimal("1"), new BigDecimal("1000"));
        order.setId(55L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(55L)).thenReturn(Optional.of(order));

        orderService.matchOrder(55L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.MATCHED);
        verify(assetService).increaseAssetSizeAndUsableSize(1L, "BTC", new BigDecimal("1"));
        verify(assetService).decreaseAssetSize(1L, "TRY", new BigDecimal("1000"));
        verify(orderRepository).save(order);
    }

    @Test
    void shouldMatchBuyOrderWhenAssetNotFoundAndFallbackToCreate() {
        Order order = new Order(1L, "NEW_ASSET", OrderSide.BUY, new BigDecimal("1"), new BigDecimal("10"));
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new RuntimeException()).when(assetService).increaseAssetSizeAndUsableSize(1L, "NEW_ASSET", new BigDecimal("1"));

        orderService.matchOrder(1L);

        verify(assetService).createOrUpdateAsset(1L, "NEW_ASSET", new BigDecimal("1"), new BigDecimal("1"));
    }

    @Test
    void shouldReturnPendingOrders() {
        List<Order> pendingOrders = List.of(new Order(), new Order());
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        List<Order> result = orderService.getPendingOrders();

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnAllOrders() {
        List<Order> allOrders = List.of(new Order(), new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(allOrders);

        List<Order> result = orderService.getAllOrders();

        assertThat(result).hasSize(3);
    }
}
