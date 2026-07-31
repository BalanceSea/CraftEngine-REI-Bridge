package net.mountainseal.cereibridge.client.rei;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class CeCraftingCategory implements DisplayCategory<CeCraftingDisplay> {
    @Override
    public CategoryIdentifier<? extends CeCraftingDisplay> getCategoryIdentifier() {
        return CeReiPlugin.CRAFTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.craftengine_rei_bridge.crafting");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.CRAFTING_TABLE);
    }

    @Override
    public List<Widget> setupDisplay(CeCraftingDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 27);
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(start.x + 60, start.y + 18)));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 95, start.y + 19)));

        List<Slot> slots = new ArrayList<>(9);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                slots.add(Widgets.createSlot(new Point(start.x + 1 + column * 18, start.y + 1 + row * 18)).markInput());
            }
        }
        for (int row = 0; row < display.height(); row++) {
            for (int column = 0; column < display.width(); column++) {
                int source = row * display.width() + column;
                int target = row * 3 + column;
                slots.get(target).entries(display.getInputEntries().get(source));
            }
        }
        widgets.addAll(slots);
        widgets.add(Widgets.createSlot(new Point(start.x + 95, start.y + 19))
                .entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        if (display.shapeless()) {
            widgets.add(Widgets.createShapelessIcon(bounds));
        }
        return widgets;
    }
}
