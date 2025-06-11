package dev.sami.brokagemodule.controller;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/all")
    public ResponseEntity<List<Asset>> getAllAssets() {
        log.debug("REST request to get all assets");
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Asset>> getAssetsByCustomerId(@PathVariable Long customerId) {
        log.debug("REST request to get assets for customer: {}", customerId);
        List<Asset> assets = assetService.getAssetsByCustomer(customerId);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/customer/{customerId}/search")
    public ResponseEntity<List<Asset>> searchAssetsByCustomerIdAndName(
            @PathVariable Long customerId,
            @RequestParam String assetName) {
        log.debug("REST request to search assets for customer: {} with name: {}", customerId, assetName);
        List<Asset> assets = assetService.searchAssetsByCustomerAndName(customerId, assetName);
        return ResponseEntity.ok(assets);
    }

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<Asset> createOrUpdateAsset(
            @PathVariable Long customerId,
            @RequestParam String assetName,
            @RequestParam BigDecimal size,
            @RequestParam BigDecimal usableSize) {
        log.debug("REST request to create/update asset for customer: {}", customerId);
        Asset asset = assetService.createOrUpdateAsset(customerId, assetName, size, usableSize);
        return ResponseEntity.ok(asset);
    }
} 