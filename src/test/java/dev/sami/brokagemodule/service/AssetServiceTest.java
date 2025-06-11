package dev.sami.brokagemodule.service;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.domain.Customer;
import dev.sami.brokagemodule.exception.AssetNotFoundException;
import dev.sami.brokagemodule.exception.CustomerNotFoundException;
import dev.sami.brokagemodule.exception.InsufficientFundsException;
import dev.sami.brokagemodule.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AssetService assetService;

    @Test
    void getAllAssetsTest() {
        List<Asset> assets = List.of(new Asset(1L, 1L, "Stock A", BigDecimal.TEN, BigDecimal.TEN));
        when(assetRepository.findAll()).thenReturn(assets);

        List<Asset> result = assetService.getAllAssets();

        assertEquals(assets, result);
        verify(assetRepository).findAll();
    }

    @Test
    void getAssetsByCustomerTest() {
        Long customerId = 1L;
        List<Asset> assets = List.of(new Asset(1L, customerId, "Bond B", BigDecimal.ONE, BigDecimal.ONE));
        when(assetRepository.findByCustomerId(customerId)).thenReturn(assets);

        List<Asset> result = assetService.getAssetsByCustomer(customerId);

        assertEquals(assets, result);
        verify(assetRepository).findByCustomerId(customerId);
    }

    @Test
    void searchAssetsByCustomerAndNameTest() {
        Long customerId = 1L;
        String assetName = "Gold";
        List<Asset> assets = List.of(new Asset(2L, customerId, "Gold", BigDecimal.ONE, BigDecimal.ONE));
        when(assetRepository.findByCustomerIdAndAssetNameContainingIgnoreCase(customerId, assetName)).thenReturn(assets);

        List<Asset> result = assetService.searchAssetsByCustomerAndName(customerId, assetName);

        assertEquals(assets, result);
        verify(assetRepository).findByCustomerIdAndAssetNameContainingIgnoreCase(customerId, assetName);
    }

    @Test
    void getAssetByCustomerAndNameTest() {
        Long customerId = 1L;
        String assetName = "Silver";
        Asset asset = new Asset(3L, customerId, assetName, BigDecimal.ONE, BigDecimal.ONE);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        Asset result = assetService.getAssetByCustomerAndName(customerId, assetName);

        assertEquals(asset, result);
        verify(assetRepository).findByCustomerIdAndAssetName(customerId, assetName);
    }

    @Test
    void getAssetByCustomerAndNameThrowsTest() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "X")).thenReturn(Optional.empty());

        assertThrows(AssetNotFoundException.class, () -> assetService.getAssetByCustomerAndName(1L, "X"));
    }

    @Test
    void createOrUpdateAssetNewTest() {
        Long customerId = 1L;
        String assetName = "NewAsset";
        BigDecimal size = BigDecimal.TEN;
        BigDecimal usableSize = BigDecimal.ONE;
        Customer customer = new Customer(1L, "Sami", "Sahin", "111", "Istanbul");
        when(customerService.getCustomerById(customerId)).thenReturn(customer);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());
        Asset asset = Asset.builder().customerId(customerId).assetName(assetName).size(size).usableSize(usableSize).build();
        when(assetRepository.save(any())).thenReturn(asset);

        Asset result = assetService.createOrUpdateAsset(customerId, assetName, size, usableSize);

        assertEquals(asset, result);
    }

    @Test
    void createOrUpdateAssetThrowsTest() {
        Long customerId = 1L;
        when(customerService.getCustomerById(customerId)).thenReturn(null);

        assertThrows(CustomerNotFoundException.class, () -> assetService.createOrUpdateAsset(customerId, "X", BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    void increaseAssetUsableSizeTest() {
        Long customerId = 1L;
        String assetName = "Test";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.TEN, BigDecimal.ONE);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        assetService.increaseAssetUsableSize(customerId, assetName, BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(2), asset.getUsableSize());
        verify(assetRepository).save(asset);
    }

    @Test
    void increaseAssetSizeAndUsableSizeTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.TEN, BigDecimal.TEN);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        assetService.increaseAssetSizeAndUsableSize(customerId, assetName, BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(11), asset.getSize());
        assertEquals(BigDecimal.valueOf(11), asset.getUsableSize());
        verify(assetRepository).save(asset);
    }

    @Test
    void decreaseAssetUsableSizeTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.TEN, BigDecimal.TEN);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        assetService.decreaseAssetUsableSize(customerId, assetName, BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(9), asset.getUsableSize());
        verify(assetRepository).save(asset);
    }

    @Test
    void decreaseAssetUsableSizeThrowsTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.TEN, BigDecimal.ZERO);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        assertThrows(InsufficientFundsException.class, () -> assetService.decreaseAssetUsableSize(customerId, assetName, BigDecimal.ONE));
    }

    @Test
    void decreaseAssetSizeTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.TEN, BigDecimal.TEN);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        assetService.decreaseAssetSize(customerId, assetName, BigDecimal.ONE);

        assertEquals(BigDecimal.valueOf(9), asset.getSize());
        verify(assetRepository).save(asset);
    }

    @Test
    void decreaseAssetSizeThrowsTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.ZERO, BigDecimal.ZERO);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        assertThrows(InsufficientFundsException.class, () -> assetService.decreaseAssetSize(customerId, assetName, BigDecimal.ONE));
    }

    @Test
    void getCustomerAssetTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.ONE, BigDecimal.ONE);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        Asset result = assetService.getCustomerAsset(customerId, assetName);

        assertEquals(asset, result);
    }

    @Test
    void isAssetInsufficientFalseTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        Asset asset = new Asset(1L, customerId, assetName, BigDecimal.ONE, BigDecimal.TEN);
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.of(asset));

        boolean result = assetService.isAssetInsufficient(customerId, assetName, BigDecimal.ONE);

        assertFalse(result);
    }

    @Test
    void isAssetInsufficientTrueTest() {
        Long customerId = 1L;
        String assetName = "Asset1";
        when(assetRepository.findByCustomerIdAndAssetName(customerId, assetName)).thenReturn(Optional.empty());

        boolean result = assetService.isAssetInsufficient(customerId, assetName, BigDecimal.ONE);

        assertTrue(result);
    }
}
