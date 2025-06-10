package dev.sami.brokagemodule.mapper;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.dto.AssetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssetMapperTest {

    private AssetMapper assetMapper;

    @BeforeEach
    void setUp() {
        assetMapper = new AssetMapper();
    }

    @Test
    void toAssetResponse_shouldMapAssetToAssetResponse() {
        Asset asset = new Asset(1L, "AAPL", new BigDecimal("100"), new BigDecimal("50"));

        AssetResponse response = assetMapper.toAssetResponse(asset);

        assertNotNull(response);
        assertEquals(asset.getCustomerId(), response.getCustomerId());
        assertEquals(asset.getAssetName(), response.getAssetName());
        assertEquals(asset.getSize(), response.getSize());
        assertEquals(asset.getUsableSize(), response.getUsableSize());
    }

    @Test
    void toAssetResponse_shouldReturnNullForNullAsset() {
        Asset asset = null;

        AssetResponse response = assetMapper.toAssetResponse(asset);

        assertNull(response);
    }

    @Test
    void toAssetResponse_shouldMapTryAssetCorrectly() {
        Asset tryAsset = new Asset(1L, "TRY", new BigDecimal("10000"), new BigDecimal("5000"));

        AssetResponse response = assetMapper.toAssetResponse(tryAsset);

        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals("TRY", response.getAssetName());
        assertEquals(new BigDecimal("10000"), response.getSize());
        assertEquals(new BigDecimal("5000"), response.getUsableSize());
    }
} 