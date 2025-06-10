package dev.sami.brokagemodule.mapper;

import dev.sami.brokagemodule.domain.Asset;
import dev.sami.brokagemodule.dto.AssetResponse;
import org.springframework.stereotype.Component;

@Component
public class AssetMapper {
    
    public AssetResponse toAssetResponse(Asset asset) {
        return new AssetResponse(
                asset.getId(),
                asset.getCustomer().getCustomerId(),
                asset.getAssetName(),
                asset.getSize(),
                asset.getUsableSize()
        );
    }
} 