package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTraitLeveled;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitRaging extends AbstractTraitLeveled {
    protected final float leveledDamage = 4f;

    public TraitRaging() {
        super("raging", 0xc70000, 3, 1);
    }

    @Override
    public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
        ModifierNBT data = new ModifierNBT(TinkerUtil.getModifierTag(tool, name));
        float healthRatio = player.getHealth() / player.getMaxHealth();
        newDamage += (leveledDamage * data.level) * (1 - healthRatio);
        return super.damage(tool, player, target, damage, newDamage, isCritical);
    }
}
