package dev.sami.brokagemodule.repository;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    
    List<Asset> findByCustomer(Customer customer);
    
    Optional<Asset> findByCustomerAndAssetName(Customer customer, String assetName);
    
    List<Asset> findByCustomerAndAssetNameContainingIgnoreCase(Customer customer, String assetName);
} 