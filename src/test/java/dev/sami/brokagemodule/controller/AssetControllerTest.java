package dev.sami.brokagemodule.controller;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.service.AssetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    @Test
    void getAllAssetsTest() {
        List<Asset> expectedAssets = List.of(
                new Asset(1L, 1L, "Stock A", BigDecimal.valueOf(100), BigDecimal.valueOf(80)),
                new Asset(2L, 2L, "Bond B", BigDecimal.valueOf(200), BigDecimal.valueOf(190))
        );
        when(assetService.getAllAssets()).thenReturn(expectedAssets);

        ResponseEntity<List<Asset>> response = assetController.getAllAssets();

        assertEquals(expectedAssets, response.getBody());
        verify(assetService, times(1)).getAllAssets();
    }

    @Test
    void getAssetsByCustomerIdTest() {
        Long customerId = 1L;
        List<Asset> expectedAssets = List.of(
                new Asset(3L, customerId, "ETF C", BigDecimal.valueOf(50), BigDecimal.valueOf(45))
        );
        when(assetService.getAssetsByCustomer(customerId)).thenReturn(expectedAssets);

        ResponseEntity<List<Asset>> response = assetController.getAssetsByCustomerId(customerId);

        assertEquals(expectedAssets, response.getBody());
        verify(assetService, times(1)).getAssetsByCustomer(customerId);
    }

    @Test
    void searchAssetsByCustomerIdAndNameTest() {
        Long customerId = 1L;
        String assetName = "Gold";
        List<Asset> expectedAssets = List.of(
                new Asset(4L, customerId, "Gold", BigDecimal.valueOf(300), BigDecimal.valueOf(280))
        );
        when(assetService.searchAssetsByCustomerAndName(customerId, assetName)).thenReturn(expectedAssets);

        ResponseEntity<List<Asset>> response = assetController.searchAssetsByCustomerIdAndName(customerId, assetName);

        assertEquals(expectedAssets, response.getBody());
        verify(assetService, times(1)).searchAssetsByCustomerAndName(customerId, assetName);
    }

    @Test
    void createOrUpdateAssetTest() {
        Long customerId = 1L;
        String assetName = "Silver";
        BigDecimal size = BigDecimal.valueOf(400);
        BigDecimal usableSize = BigDecimal.valueOf(390);
        Asset expectedAsset = new Asset(5L, customerId, assetName, size, usableSize);
        when(assetService.createOrUpdateAsset(customerId, assetName, size, usableSize)).thenReturn(expectedAsset);

        ResponseEntity<Asset> response = assetController.createOrUpdateAsset(customerId, assetName, size, usableSize);

        assertEquals(expectedAsset, response.getBody());
        verify(assetService, times(1)).createOrUpdateAsset(customerId, assetName, size, usableSize);
    }
}
