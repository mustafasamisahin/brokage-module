package dev.sami.brokagemodule.repository;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByCustomerId(Long customerId);
    
    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);
    
    List<Asset> findByCustomerIdAndAssetNameContainingIgnoreCase(Long customerId, String assetName);
} 