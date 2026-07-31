package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

public final class CeReiCommonPlugin implements REICommonPlugin {
    private static final String IDENTITY_KEY = "craftengine:id";

    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.registerGlobal((context, stack) -> {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData == null) {
                return 1L;
            }
            String identity = customData.copyTag().getStringOr(IDENTITY_KEY, "");
            return identity.isEmpty() ? 1L : identityHash(identity);
        });
    }

    private static long identityHash(String identity) {
        long hash = 0xcbf29ce484222325L;
        for (int index = 0; index < identity.length(); index++) {
            hash ^= identity.charAt(index);
            hash *= 0x100000001b3L;
        }
        return hash == 1L ? 0L : hash;
    }
}
