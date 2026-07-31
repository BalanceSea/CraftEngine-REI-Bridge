package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class CeSmithingCategory implements DisplayCategory<CeSmithingDisplay> {
    @Override
    public CategoryIdentifier<? extends CeSmithingDisplay> getCategoryIdentifier() {
        return CeReiPlugin.SMITHING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.craftengine_rei_bridge.smithing");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.SMITHING_TABLE);
    }

    @Override
    public List<Widget> setupDisplay(CeSmithingDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.getCenterX() - 62, bounds.getCenterY() - 13);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(start.x + 76, start.y + 4)));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 111, start.y + 5)));
        for (int slot = 0; slot < 3; slot++) {
            widgets.add(Widgets.createSlot(new Point(start.x + 4 + slot * 18, start.y + 5))
                    .entries(display.getInputEntries().get(slot)).markInput());
        }
        widgets.add(Widgets.createSlot(new Point(start.x + 111, start.y + 5))
                .entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }
}
