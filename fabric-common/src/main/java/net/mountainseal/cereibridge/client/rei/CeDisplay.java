package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import org.jetbrains.annotations.Nullable;

public interface CeDisplay extends Display {
    int generation();

    @Override
    @Nullable
    default DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}
