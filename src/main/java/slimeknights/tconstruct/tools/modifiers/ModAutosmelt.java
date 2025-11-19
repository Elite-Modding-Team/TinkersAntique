package slimeknights.tconstruct.tools.modifiers;

import java.util.ListIterator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTraits;

// Identical to the trait version of Autosmelt, these are separate because both modifiers and traits are registered differently
public class ModAutosmelt extends ModifierTrait {

    public ModAutosmelt() {
        super("mod_autosmelt", 0xfc0000);

        addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.harvestOnly, ModifierAspect.freeModifier);
    }

    @Override
    public boolean canApplyTogether(Enchantment enchantment) {
        return enchantment != Enchantments.SILK_TOUCH;
    }

    @Override
    public boolean canApplyTogether(IToolMod otherModifier) {
        return !otherModifier.getIdentifier().equals(TinkerTraits.autosmelt.getIdentifier())
                && !otherModifier.getIdentifier().equals(TinkerModifiers.modSilktouch.getIdentifier());
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
        if (ToolHelper.isToolEffective2(tool, event.getState())) {
            // go through the drops and replace them with their furnace'd variant if applicable
            ListIterator<ItemStack> iter = event.getDrops().listIterator();
            while (iter.hasNext()) {
                ItemStack drop = iter.next();
                ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(drop);
                if (!smelted.isEmpty()) {
                    smelted = smelted.copy();
                    smelted.setCount(drop.getCount());
                    int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
                    if (Config.autosmeltlapis && fortune > 0) {
                        smelted.setCount(smelted.getCount() * random.nextInt(fortune + 1) + 1);
                    }
                    iter.set(smelted);

                    // drop XP for it
                    float xp = FurnaceRecipes.instance().getSmeltingExperience(smelted);
                    if (xp < 1 && Math.random() < xp) {
                        xp += 1f;
                    }
                    if (xp >= 1f) {
                        event.getState().getBlock().dropXpOnBlockBreak(event.getWorld(), event.getPos(), (int) xp);
                    }
                }
            }
        }
    }

    @Override
    public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
        if (world.isRemote && wasEffective) {
            for (int i = 0; i < 3; i++) {
                world.spawnParticle(EnumParticleTypes.FLAME,
                        pos.getX() + random.nextDouble(),
                        pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(),
                        0.0D, 0.0D, 0.0D);
            }
        }
    }
}
