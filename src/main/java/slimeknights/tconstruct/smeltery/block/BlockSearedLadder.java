package slimeknights.tconstruct.smeltery.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.smeltery.block.BlockEnumSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockSearedGlass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraft.block.BlockHorizontal.FACING;

// Courtesy of jbredwards
public class BlockSearedLadder extends BlockEnumSmeltery<BlockSearedGlass.GlassType> {
    public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
    public static final AxisAlignedBB BOTTOM_BOX = box(0, 0, 0, 16, 2, 16);
    public static final List<AxisAlignedBB>
            NORTH_BOX = ImmutableList.of(
            box(0, 0, 2, 16, 16, 16),
            box(14, 0, 0, 16, 16, 2),
            box(0, 0, 0, 2, 16, 2)
    ),
            SOUTH_BOX = ImmutableList.of(
                    box(0, 0, 0, 16, 16, 14),
                    box(0, 0, 14, 2, 16, 16),
                    box(14, 0, 14, 16, 16, 16)
            ),
            EAST_BOX = ImmutableList.of(
                    box(0, 0, 0, 14, 16, 16),
                    box(14, 0, 14, 16, 16, 16),
                    box(14, 0, 0, 16, 16, 2)
            ),
            WEST_BOX = ImmutableList.of(
                    box(2, 0, 0, 16, 16, 16),
                    box(0, 0, 0, 2, 16, 2),
                    box(0, 0, 14, 2, 16, 16));

    public BlockSearedLadder() {
        super(Material.ROCK, BlockSearedGlass.TYPE, BlockSearedGlass.GlassType.class);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockSearedGlass.TYPE, BOTTOM, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() << 1 | (state.getValue(BOTTOM) ? 1 : 0);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(BOTTOM, (meta & 1) == 1)
                .withProperty(FACING, EnumFacing.getHorizontal(meta >> 1));
    }

    @Nonnull
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        final IBlockState state = getDefaultState().withProperty(FACING, placer.isSneaking()
                ? placer.getHorizontalFacing() : placer.getHorizontalFacing().getOpposite());

        return state.withProperty(BOTTOM, shouldBeBottom(state, worldIn.getBlockState(pos.down())));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return state.isSideSolid(world, pos, face);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return state.getBlockFaceShape(world, pos, side) == BlockFaceShape.SOLID;
    }

    @Nonnull
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face == EnumFacing.DOWN && state.getValue(BOTTOM)) return BlockFaceShape.SOLID;
        return face.getAxis().isVertical() || state.getValue(FACING) == face
                ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GRAY + I18n.format("tile.searedladder.seared_ladder.tooltip"));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (pos.down().equals(fromPos)) {
            final boolean isBottom = state.getValue(BOTTOM);
            if (isBottom != shouldBeBottom(state, worldIn.getBlockState(fromPos)))
                worldIn.setBlockState(pos, state.withProperty(BOTTOM, !isBottom));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        getCollisionBoxList(state).forEach(aabb -> addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb));
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        final List<RayTraceResult> list = getCollisionBoxList(blockState).stream()
                .map(aabb -> rayTrace(pos, start, end, aabb))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (list.isEmpty()) return null;
        RayTraceResult furthest = null;
        double dist = -1;

        for (RayTraceResult trace : list) {
            final double newDist = trace.hitVec.squareDistanceTo(end);
            if (newDist > dist) {
                furthest = trace;
                dist = newDist;
            }
        }

        return furthest;
    }

    @Nonnull
    public List<AxisAlignedBB> getCollisionBoxList(IBlockState state) {
        final List<AxisAlignedBB> collisions = new ArrayList<>();
        switch (state.getValue(FACING)) {
            case NORTH:
                collisions.addAll(NORTH_BOX);
                break;
            case SOUTH:
                collisions.addAll(SOUTH_BOX);
                break;
            case EAST:
                collisions.addAll(EAST_BOX);
                break;
            case WEST:
                collisions.addAll(WEST_BOX);
            default:
        }

        if (state.getValue(BOTTOM)) collisions.add(BOTTOM_BOX);
        return collisions;
    }

    public boolean shouldBeBottom(IBlockState state, IBlockState down) {
        return !isAssociatedBlock(down.getBlock()) || down.getValue(FACING) != state.getValue(FACING);
    }

    @Nonnull
    static AxisAlignedBB box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return new AxisAlignedBB(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }
}
