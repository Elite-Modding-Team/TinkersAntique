package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockCommonOre extends EnumBlock<BlockCommonOre.OreTypes> {

    public static final PropertyEnum<OreTypes> TYPE = PropertyEnum.create("type", OreTypes.class);

    public BlockCommonOre() {
        super(Material.ROCK, TYPE, OreTypes.class);
        setCreativeTab(TinkerRegistry.tabWorld);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        OreTypes type = state.getValue(TYPE);
        return type.getHardness();
    }
    
    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        OreTypes type = state.getValue(TYPE);
        return type.getMiningLevel();
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public enum OreTypes implements IStringSerializable, EnumBlock.IEnumMeta {
        COPPER(3.0F, 1),
        TIN(3.0F, 1),
        ALUMINUM(3.0F, 1);

        private final int meta;
        private final float hardness;
        private final int miningLevel;

        OreTypes(float hardness, int miningLevel) {
            this.meta = ordinal();
            this.hardness = hardness;
            this.miningLevel = miningLevel;
        }

        public float getHardness() {
            return hardness;
        }

        public int getMiningLevel() {
            return miningLevel;
        }

        @Override
        public String getName() {
            return this.toString().toLowerCase(Locale.US);
        }

        @Override
        public int getMeta() {
            return meta;
        }
    }
}
