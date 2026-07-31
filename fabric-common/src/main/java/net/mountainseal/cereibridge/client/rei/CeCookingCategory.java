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
import net.minecraft.world.item.Item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class CeCookingCategory implements DisplayCategory<CeCookingDisplay> {
    private final CategoryIdentifier<CeCookingDisplay> categoryIdentifier;
    private final String titleKey;
    private final Item icon;
    private final int defaultCookingTime;
    private final boolean showExperience;

    public CeCookingCategory(CategoryIdentifier<CeCookingDisplay> categoryIdentifier, String titleKey,
                             Item icon, int defaultCookingTime, boolean showExperience) {
        this.categoryIdentifier = categoryIdentifier;
        this.titleKey = titleKey;
        this.icon = icon;
        this.defaultCookingTime = defaultCookingTime;
        this.showExperience = showExperience;
    }

    @Override
    public CategoryIdentifier<? extends CeCookingDisplay> getCategoryIdentifier() {
        return categoryIdentifier;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(titleKey);
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(icon);
    }

    @Override
    public List<Widget> setupDisplay(CeCookingDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 61, start.y + 9)));
        widgets.add(Widgets.createBurningFire(new Point(start.x + 1, start.y + 20)).animationDurationMS(10_000));

        int cookingTime = display.cookingTime() > 0 ? display.cookingTime() : defaultCookingTime;
        Component details = showExperience
                ? Component.translatable("category.craftengine_rei_bridge.cooking.time_and_xp",
                decimalFormat.format(display.experience()), decimalFormat.format(cookingTime / 20.0))
                : Component.translatable("category.craftengine_rei_bridge.cooking.time",
                decimalFormat.format(cookingTime / 20.0));
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5), details)
                .noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
        widgets.add(Widgets.createArrow(new Point(start.x + 24, start.y + 8))
                .animationDurationTicks(cookingTime));
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y + 1))
                .entries(display.getInputEntries().get(0)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y + 9))
                .entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 49;
    }
}
