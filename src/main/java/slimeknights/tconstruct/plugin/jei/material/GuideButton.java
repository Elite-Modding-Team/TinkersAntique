package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.gui.elements.DrawableIngredient;
import mezz.jei.gui.elements.GuiIconButtonSmall;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.book.TinkerBook;

import java.util.List;

public class GuideButton extends GuiIconButtonSmall {
    private final ItemStack book;

    public GuideButton(int buttonId, int x, int y, int widthIn, int heightIn, String category, String material, List<String> parts, ItemStack book) {
        super(buttonId, x, y, widthIn, heightIn, new DrawableIngredient<>(book, new ItemStackRenderer()));
        this.book = getBookStack(book, category, material, parts);
    }

    public String getTooltip() {
        return I18n.format("gui.jei.material.openbook", this.book.getDisplayName());
    }

    public void openBook() {
        TinkerBook.INSTANCE.openGui(book);
    }

    public boolean mousePressed(int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    private ItemStack getBookStack(ItemStack guide, String category, String material, List<String> parts) {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound mantle = new NBTTagCompound();
        NBTTagCompound book = new NBTTagCompound();

        String type = null;
        /*
         * The problem with linking the other types to the correct page of the book is that the
         * pages with bow materials have a weird naming convention, that makes it impossible to
         * link to the proper page. Example: "bowmaterials.bow_cactus_bone_obsidian".
         * That is why I just link to the general material overview page for now.
         *
         * Maybe this can be improved in the future, when the guide book is reworked.
         */
        switch (category) {
            case Util.MODID + ":projectile_stats":
                if (parts.size() == 1) {
                    switch (parts.get(0)) {
                        case "fletching":
                            type = "bowmaterials.page3";
                            break;
                        case "shaft":
                            type = "bowmaterials.page2";
                            break;
                    }
                }
                break;
            case Util.MODID + ":ranged_stats":
                if (parts.size() == 1) {
                    switch (parts.get(0)) {
                        case "bow":
                            type = "bowmaterials.page0";
                            break;
                        case "bowstring":
                            type = "bowmaterials.page1";
                            break;
                    }
                }
                break;
        }

        book.setString("page", String.format("%s", type == null ? "materials." + material : type));
        mantle.setTag("book", book);
        compound.setTag("mantle", mantle);

        guide.setTagCompound(compound);

        return guide;
    }
}
