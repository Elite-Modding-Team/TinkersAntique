package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.gui.elements.DrawableIngredient;
import mezz.jei.gui.elements.GuiIconButtonSmall;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.book.TinkerBook;

import javax.annotation.Nonnull;

public class GuideButton extends GuiIconButtonSmall {
    private final ItemStack book;

    public GuideButton(int buttonId, int x, int y, int widthIn, int heightIn, String material, ItemStack book) {
        super(buttonId, x, y, widthIn, heightIn, new DrawableIngredient<>(book, new ItemStackRenderer()));
        this.book = getBookStack(book, material);
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

    public void setHover(int mX, int mY) {
        this.hovered = mousePressed(mX, mY);
    }

    private ItemStack getBookStack(ItemStack guide, String material) {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound mantle = new NBTTagCompound();
        NBTTagCompound book = new NBTTagCompound();

        book.setString("page", String.format("materials.%s", material));
        mantle.setTag("book", book);
        compound.setTag("mantle", mantle);

        guide.setTagCompound(compound);

        return guide;
    }
}
