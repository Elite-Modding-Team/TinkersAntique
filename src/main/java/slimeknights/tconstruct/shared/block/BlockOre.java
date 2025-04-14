package slimeknights.tconstruct.shared.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockOre extends EnumBlock<BlockOre.OreTypes> {

  public static final PropertyEnum<OreTypes> TYPE = PropertyEnum.create("type", OreTypes.class);

  public BlockOre() {
    this(Material.ROCK);
  }

  public BlockOre(Material material) {
    super(material, TYPE, OreTypes.class);

    setHardness(10f);
    setHarvestLevel("pickaxe", Config.netherOresMiningLevel);
    setCreativeTab(TinkerRegistry.tabWorld);
  }

  @Nonnull
  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }

  public enum OreTypes implements IStringSerializable, EnumBlock.IEnumMeta {
    COBALT,
    ARDITE;

    public final int meta;

    OreTypes() {
      meta = ordinal();
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
