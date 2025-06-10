package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.domain.Customer;
import dev.sami.brokagemodule.exception.AssetNotFoundException;
import dev.sami.brokagemodule.repository.AssetRepository;
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
public class AssetService {
    
    private final AssetRepository assetRepository;
    
    public List<Asset> getAssetsByCustomer(Customer customer) {
        log.debug("Getting assets for customer: {}", customer.getCustomerId());
        return assetRepository.findByCustomer(customer);
    }
    
    public List<Asset> searchAssetsByCustomerAndName(Customer customer, String assetName) {
        log.debug("Searching assets for customer: {} with name: {}", customer.getCustomerId(), assetName);
        return assetRepository.findByCustomerAndAssetNameContainingIgnoreCase(customer, assetName);
    }
    
    public Asset getAssetByCustomerAndName(Customer customer, String assetName) {
        log.debug("Getting asset {} for customer: {}", assetName, customer.getCustomerId());
        return assetRepository.findByCustomerAndAssetName(customer, assetName)
                .orElseThrow(() -> new AssetNotFoundException(
                        String.format("Asset %s not found for customer %d", assetName, customer.getCustomerId())));
    }
    
    public Asset createOrUpdateAsset(Customer customer, String assetName, BigDecimal size, BigDecimal usableSize) {
        log.debug("Creating/updating asset {} for customer: {}", assetName, customer.getCustomerId());
        
        Asset asset = assetRepository.findByCustomerAndAssetName(customer, assetName)
                .orElse(new Asset(customer, assetName, BigDecimal.ZERO, BigDecimal.ZERO));
        
        asset.setSize(size);
        asset.setUsableSize(usableSize);
        
        return assetRepository.save(asset);
    }
    
    public void updateAssetUsableSize(Customer customer, String assetName, BigDecimal newUsableSize) {
        log.debug("Updating usable size for asset {} of customer {} to {}", assetName, customer.getCustomerId(), newUsableSize);
        
        Asset asset = getAssetByCustomerAndName(customer, assetName);
        asset.setUsableSize(newUsableSize);
        assetRepository.save(asset);
    }
    
    public void increaseAssetUsableSize(Customer customer, String assetName, BigDecimal amount) {
        log.debug("Increasing usable size for asset {} of customer {} by {}", assetName, customer.getCustomerId(), amount);
        
        Asset asset = getAssetByCustomerAndName(customer, assetName);
        asset.setUsableSize(asset.getUsableSize().add(amount));
        assetRepository.save(asset);
    }
    
    public void decreaseAssetUsableSize(Customer customer, String assetName, BigDecimal amount) {
        log.debug("Decreasing usable size for asset {} of customer {} by {}", assetName, customer.getCustomerId(), amount);
        
        Asset asset = getAssetByCustomerAndName(customer, assetName);
        BigDecimal newUsableSize = asset.getUsableSize().subtract(amount);
        
        if (newUsableSize.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient usable size for asset: " + assetName);
        }
        
        asset.setUsableSize(newUsableSize);
        assetRepository.save(asset);
    }
} 