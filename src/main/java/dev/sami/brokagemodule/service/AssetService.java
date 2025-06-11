package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.exception.AssetNotFoundException;
import dev.sami.brokagemodule.exception.CustomerNotFoundException;
import dev.sami.brokagemodule.exception.InsufficientFundsException;
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
    private final CustomerService customerService;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public List<Asset> getAssetsByCustomer(Long customerId) {
        log.debug("Getting assets for customer: {}", customerId);
        return assetRepository.findByCustomerId(customerId);
    }

    public List<Asset> searchAssetsByCustomerAndName(Long customerId, String assetName) {
        log.debug("Searching assets for customer: {} with name: {}", customerId, assetName);
        return assetRepository.findByCustomerIdAndAssetNameContainingIgnoreCase(customerId, assetName);
    }

    public Asset getAssetByCustomerAndName(Long customerId, String assetName) {
        log.debug("Getting asset {} for customer: {}", assetName, customerId);
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new AssetNotFoundException(
                        String.format("Asset %s not found for customer %d", assetName, customerId)));
    }

    public Asset createOrUpdateAsset(Long customerId, String assetName, BigDecimal size, BigDecimal usableSize) {
        log.debug("Creating/updating asset {} for customer: {}", assetName, customerId);

        if (customerService.getCustomerById(customerId) == null) {
            throw new CustomerNotFoundException(String.format("Customer %s not found", customerId));
        }

        Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElse(Asset.builder().assetName(assetName).customerId(customerId).size(size).usableSize(usableSize).build());

        return assetRepository.save(asset);
    }

    public void increaseAssetUsableSize(Long customerId, String assetName, BigDecimal amount) {
        log.debug("Increasing usable size for asset {} of customer {} by {}", assetName, customerId, amount);

        Asset asset = getAssetByCustomerAndName(customerId, assetName);
        asset.setUsableSize(asset.getUsableSize().add(amount));
        assetRepository.save(asset);
    }

    public void increaseAssetSizeAndUsableSize(Long customerId, String assetName, BigDecimal amount) {
        log.debug("Increasing size and usable size for asset {} of customer {} by {}", assetName, customerId, amount);

        Asset asset = getAssetByCustomerAndName(customerId, assetName);
        asset.setSize(asset.getSize().add(amount));
        asset.setUsableSize(asset.getUsableSize().add(amount));
        assetRepository.save(asset);
    }

    public void decreaseAssetUsableSize(Long customerId, String assetName, BigDecimal amount) {
        log.debug("Decreasing usable size for asset {} of customer {} by {}", assetName, customerId, amount);

        Asset asset = getAssetByCustomerAndName(customerId, assetName);
        BigDecimal newUsableSize = asset.getUsableSize().subtract(amount);

        if (newUsableSize.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient usable size for asset: " + assetName);
        }

        asset.setUsableSize(newUsableSize);
        assetRepository.save(asset);
    }

    public void decreaseAssetSize(Long customerId, String assetName, BigDecimal amount) {
        log.debug("Decreasing size for asset {} of customer {} by {}", assetName, customerId, amount);

        Asset asset = getAssetByCustomerAndName(customerId, assetName);
        BigDecimal newSize = asset.getSize().subtract(amount);
        if (newSize.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient size for asset: " + assetName);
        }
        asset.setSize(newSize);
        assetRepository.save(asset);
    }

    public Asset getCustomerAsset(Long customerId, String assetName) {
        log.debug("Getting asset {} for customer: {}", assetName, customerId);
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new AssetNotFoundException(
                        String.format("Asset %s not found for customer %d", assetName, customerId)));
    }

    public boolean isAssetInsufficient(Long customerId, String assetName, BigDecimal requiredAmount) {
        try {
            Asset asset = getCustomerAsset(customerId, assetName);
            return asset.getUsableSize().compareTo(requiredAmount) < 0;
        } catch (RuntimeException e) {
            return true;
        }
    }
} 