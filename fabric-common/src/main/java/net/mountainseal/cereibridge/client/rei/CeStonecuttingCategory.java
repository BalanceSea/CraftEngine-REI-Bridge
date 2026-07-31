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

public final class CeStonecuttingCategory implements DisplayCategory<CeStonecuttingDisplay> {
    @Override
    public CategoryIdentifier<? extends CeStonecuttingDisplay> getCategoryIdentifier() {
        return CeReiPlugin.STONECUTTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.craftengine_rei_bridge.stonecutting");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.STONECUTTER);
    }

    @Override
    public List<Widget> setupDisplay(CeStonecuttingDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(start.x + 27, start.y + 4)));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 61, start.y + 5)));
        widgets.add(Widgets.createSlot(new Point(start.x + 4, start.y + 5))
                .entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y + 5))
                .entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }
}
