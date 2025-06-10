package dev.sami.brokagemodule.repository;

import dev.sami.brokagemodule.domain.Order;
import dev.sami.brokagemodule.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
    
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId " +
           "AND o.createDate >= :startDate AND o.createDate <= :endDate " +
           "AND (:status IS NULL OR o.status = :status)")
    List<Order> findByCustomerIdAndDateRangeAndStatus(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") OrderStatus status);
    
    List<Order> findByStatus(OrderStatus status);
} 