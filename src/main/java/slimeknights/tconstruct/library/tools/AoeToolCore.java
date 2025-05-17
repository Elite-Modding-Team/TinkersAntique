package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.utils.ToolHelper;

public abstract class AoeToolCore extends TinkerToolCore implements IAoeTool {

  public AoeToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.AOE);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 1, 1, 1);
  }

  @Override
  public boolean isAoeHarvestTool() {
    return true;
  }

  public EnumActionResult doMakePath(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if(ToolHelper.isBroken(stack)) {
      return EnumActionResult.FAIL;
    }

    EnumActionResult result = Items.DIAMOND_SHOVEL.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    if(result == EnumActionResult.SUCCESS) {
      TinkerToolEvent.OnShovelMakePath.fireEvent(stack, player, world, pos);
    }

    // only do the AOE path if the selected block is grass or grass path
    Block block = world.getBlockState(pos).getBlock();
    if(block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
      for(BlockPos aoePos : getAOEBlocks(stack, world, player, pos)) {
        // stop if the tool breaks during the process
        if(ToolHelper.isBroken(stack)) {
          break;
        }

        EnumActionResult aoeResult = Items.DIAMOND_SHOVEL.onItemUse(player, world, aoePos, hand, facing, hitX, hitY, hitZ);
        // if we pass on an earlier block, check if another block succeeds here instead
        if(result != EnumActionResult.SUCCESS) {
          result = aoeResult;
        }

        if(aoeResult == EnumActionResult.SUCCESS) {
          TinkerToolEvent.OnShovelMakePath.fireEvent(stack, player, world, aoePos);
        }
      }
    }

    return result;
  }

  public EnumActionResult doTill(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if(ToolHelper.isBroken(stack)) {
      return EnumActionResult.FAIL;
    }

    EnumActionResult ret = useHoe(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
    for(BlockPos blockPos : getAOEBlocks(stack, world, player, pos)) {
      if(ToolHelper.isBroken(stack)) {
        break;
      }

      EnumActionResult ret2 = useHoe(stack, player, world, blockPos, hand, facing, hitX, hitY, hitZ);
      if(ret != EnumActionResult.SUCCESS) {
        ret = ret2;
      }
    }

    if(ret == EnumActionResult.SUCCESS) {
      TinkerToolEvent.OnMattockHoe.fireEvent(stack, player, world, pos);
    }

    return ret;
  }

  private EnumActionResult useHoe(ItemStack stack, EntityPlayer player, World world, BlockPos blockPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    // make sure no damage is taken
    int damage = stack.getItemDamage();
    EnumActionResult ret = Items.DIAMOND_HOE.onItemUse(player, world, blockPos, hand, facing, hitX, hitY, hitZ);
    stack.setItemDamage(damage);

    // do tinkers damaging
    if(!world.isRemote && ret == EnumActionResult.SUCCESS) {
      ToolHelper.damageTool(stack, 1, player);
    }
    return ret;
  }
}
