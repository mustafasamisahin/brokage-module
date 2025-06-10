package dev.sami.brokagemodule.controller;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.domain.Customer;
import dev.sami.brokagemodule.dto.AssetResponse;
import dev.sami.brokagemodule.mapper.AssetMapper;
import dev.sami.brokagemodule.service.AssetService;
import dev.sami.brokagemodule.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetController {
    
    private final AssetService assetService;
    private final CustomerService customerService;
    private final AssetMapper assetMapper;
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Asset>> getAssetsByCustomerId(@PathVariable Long customerId) {
        log.debug("REST request to get assets for customer: {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
        List<Asset> assets = assetService.getAssetsByCustomer(customer);
        return ResponseEntity.ok(assets);
    }
    
    @GetMapping("/customer/{customerId}/search")
    public ResponseEntity<List<Asset>> searchAssetsByCustomerIdAndName(
            @PathVariable Long customerId,
            @RequestParam String assetName) {
        log.debug("REST request to search assets for customer: {} with name: {}", customerId, assetName);
        Customer customer = customerService.getCustomerById(customerId);
        List<Asset> assets = assetService.searchAssetsByCustomerAndName(customer, assetName);
        return ResponseEntity.ok(assets);
    }
    
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Asset> createOrUpdateAsset(
            @PathVariable Long customerId,
            @RequestParam String assetName,
            @RequestParam BigDecimal size,
            @RequestParam BigDecimal usableSize) {
        log.debug("REST request to create/update asset for customer: {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
        Asset asset = assetService.createOrUpdateAsset(customer, assetName, size, usableSize);
        return ResponseEntity.ok(asset);
    }
    
    @PutMapping("/customer/{customerId}/usable-size")
    public ResponseEntity<Void> updateAssetUsableSize(
            @PathVariable Long customerId,
            @RequestParam String assetName,
            @RequestParam BigDecimal newUsableSize) {
        log.debug("REST request to update usable size for customer: {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
        assetService.updateAssetUsableSize(customer, assetName, newUsableSize);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/customer/{customerId}/increase-usable-size")
    public ResponseEntity<Void> increaseAssetUsableSize(
            @PathVariable Long customerId,
            @RequestParam String assetName,
            @RequestParam BigDecimal amount) {
        log.debug("REST request to increase usable size for customer: {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
        assetService.increaseAssetUsableSize(customer, assetName, amount);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/customer/{customerId}/decrease-usable-size")
    public ResponseEntity<Void> decreaseAssetUsableSize(
            @PathVariable Long customerId,
            @RequestParam String assetName,
            @RequestParam BigDecimal amount) {
        log.debug("REST request to decrease usable size for customer: {}", customerId);
        Customer customer = customerService.getCustomerById(customerId);
        assetService.decreaseAssetUsableSize(customer, assetName, amount);
        return ResponseEntity.ok().build();
    }
} 