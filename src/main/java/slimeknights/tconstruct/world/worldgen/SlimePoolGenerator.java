package slimeknights.tconstruct.world.worldgen;

import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.shared.block.BlockSlime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SlimePoolGenerator extends WorldGenerator implements IWorldGenerator {
    public static SlimePoolGenerator INSTANCE = new SlimePoolGenerator();

    public SlimePoolGenerator() {
    }

    protected boolean shouldGenerateInDimension(int id) {
        for (int dim : Config.slimePoolDimensions) {
            if (dim == id) {
                return !Config.slimePoolDimensionsIsBlacklist;
            }
        }
        return Config.slimePoolDimensionsIsBlacklist;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (!Config.genSlimePools) {
            return;
        }
        if (world.getWorldType() == WorldType.FLAT) {
            return;
        }
        if (!Config.slimePoolsOnlyGenerateInSurfaceWorlds && !world.provider.isSurfaceWorld()) {
            return;
        }
        if (!shouldGenerateInDimension(world.provider.getDimension())) {
            return;
        }
        generateSlimePool(world, random, chunkX * 16, chunkZ * 16);
    }

    public void generateSlimePool(World world, Random random, int chunkX, int chunkZ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        if (random.nextInt(Config.slimePoolRate) == 0) {
            int height = random.nextInt(Config.slimePoolHeightMax);
            pos.setPos(chunkX + 8 + random.nextInt(8), height, chunkZ + 8 + random.nextInt(8));
            this.generate(world, random, pos);
        }
        pos.setPos(chunkX + 16, 0, chunkZ + 16);
        Biome biome = world.getBiome(pos);
        if (biome.getTemperature() >= 0.8f && biome.getRainfall() >= 0.9f && random.nextInt(Config.slimePoolRate / 4 + 1) == 0) {
            int height = random.nextInt(Config.slimePoolHeightMax * 3);
            pos.setPos(chunkX + 8 + random.nextInt(8), height, chunkZ + 8 + random.nextInt(8));
            this.generate(world, random, pos);
        }
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos blockPos) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(blockPos.getX() - 8, blockPos.getY(), blockPos.getZ() - 8);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        while (y > 5 && world.isAirBlock(pos)) {
            y--;
            pos.setY(y);
        }

        if (y <= 4) {
            return false;
        }

        y -= 4;
        pos.setY(y);
        boolean[] shape = new boolean[2048];
        int shapeCount = rand.nextInt(4) + 4;
        List<BlockPos> fluidPositions = new ArrayList<>();

        for (int i = 0; i < shapeCount; ++i) {
            double xSize = rand.nextDouble() * 6.0D + 3.0D;
            double ySize = rand.nextDouble() * 4.0D + 2.0D;
            double zSize = rand.nextDouble() * 6.0D + 3.0D;
            double xCenter = rand.nextDouble() * (16.0D - xSize - 2.0D) + 1.0D + xSize / 2.0D;
            double yCenter = rand.nextDouble() * (8.0D - ySize - 4.0D) + 2.0D + ySize / 2.0D;
            double zCenter = rand.nextDouble() * (16.0D - zSize - 2.0D) + 1.0D + zSize / 2.0D;

            for (int xOffset = 1; xOffset < 15; ++xOffset) {
                for (int zOffset = 1; zOffset < 15; ++zOffset) {
                    for (int yOffset = 1; yOffset < 7; ++yOffset) {
                        double xDist = (xOffset - xCenter) / (xSize / 2.0D);
                        double yDist = (yOffset - yCenter) / (ySize / 2.0D);
                        double zDist = (zOffset - zCenter) / (zSize / 2.0D);
                        if (xDist * xDist + yDist * yDist + zDist * zDist < 1.0D) {
                            shape[(xOffset * 16 + zOffset) * 8 + yOffset] = true;
                        }
                    }
                }
            }
        }

        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    boolean isEdge = !shape[(xOffset * 16 + zOffset) * 8 + yOffset]
                            && (xOffset < 15 && shape[((xOffset + 1) * 16 + zOffset) * 8 + yOffset]
                            || xOffset > 0 && shape[((xOffset - 1) * 16 + zOffset) * 8 + yOffset]
                            || zOffset < 15 && shape[(xOffset * 16 + zOffset + 1) * 8 + yOffset]
                            || zOffset > 0 && shape[(xOffset * 16 + (zOffset - 1)) * 8 + yOffset]
                            || yOffset < 7 && shape[(xOffset * 16 + zOffset) * 8 + yOffset + 1]
                            || yOffset > 0 && shape[(xOffset * 16 + zOffset) * 8 + (yOffset - 1)]);

                    if (isEdge) {
                        pos.setPos(x + xOffset, y + yOffset, z + zOffset);
                        Material material = world.getBlockState(pos).getMaterial();

                        if (yOffset >= 4 && material.isLiquid()) {
                            return false;
                        }

                        if (yOffset < 4 && !material.isSolid() && world.getBlockState(pos).getBlock() != TinkerFluids.greenSlime.getBlock()) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    if (shape[(xOffset * 16 + zOffset) * 8 + yOffset]) {
                        pos.setPos(x + xOffset, y + yOffset, z + zOffset);
                        if (yOffset < 4) {
                            fluidPositions.add(pos.toImmutable());
                        }
                        world.setBlockState(pos, yOffset >= 4 ? Blocks.AIR.getDefaultState() : TinkerFluids.greenSlime.getBlock().getDefaultState(), 2);
                    }
                }
            }
        }

        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 4; yOffset < 8; ++yOffset) {
                    if (shape[(xOffset * 16 + zOffset) * 8 + yOffset]) {
                        pos.setPos(x + xOffset, y + yOffset - 1, z + zOffset);
                        if (world.getBlockState(pos).getBlock() == Blocks.DIRT) {
                            pos.setY(y + yOffset);
                            if (world.getLightFor(EnumSkyBlock.SKY, pos) > 0) {
                                pos.setY(0);
                                Biome biome = world.getBiome(pos.setPos(x + xOffset, 0, z + zOffset));
                                pos.setY(y + yOffset - 1);
                                if (biome.topBlock.getBlock() == Blocks.MYCELIUM) {
                                    world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState(), 2);
                                } else {
                                    world.setBlockState(pos, Blocks.GRASS.getDefaultState(), 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (TinkerFluids.greenSlime.getBlock().getDefaultState().getMaterial() == Material.WATER) {
            for (int xOffset = 0; xOffset < 16; ++xOffset) {
                for (int zOffset = 0; zOffset < 16; ++zOffset) {
                    for (int yOffset = 0; yOffset < 8; ++yOffset) {
                        boolean isEdge = !shape[(xOffset * 16 + zOffset) * 8 + yOffset]
                                && (xOffset < 15 && shape[((xOffset + 1) * 16 + zOffset) * 8 + yOffset]
                                || xOffset > 0 && shape[((xOffset - 1) * 16 + zOffset) * 8 + yOffset]
                                || zOffset < 15 && shape[(xOffset * 16 + zOffset + 1) * 8 + yOffset]
                                || zOffset > 0 && shape[(xOffset * 16 + (zOffset - 1)) * 8 + yOffset]
                                || yOffset < 7 && shape[(xOffset * 16 + zOffset) * 8 + yOffset + 1]
                                || yOffset > 0 && shape[(xOffset * 16 + zOffset) * 8 + (yOffset - 1)]);

                        if (isEdge && (yOffset < 4 || rand.nextInt(2) != 0)) {
                            pos.setPos(x + xOffset, y + yOffset, z + zOffset);
                            if (world.getBlockState(pos).getMaterial().isSolid()) {
                                pos.setY(y + yOffset + 1);
                                if (world.getBlockState(pos).getMaterial() != Material.WATER) {
                                    pos.setY(y + yOffset);
                                    world.setBlockState(pos, TinkerCommons.blockSlimeCongealed.getDefaultState().withProperty(BlockSlime.TYPE, BlockSlime.SlimeType.GREEN), 2);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!fluidPositions.isEmpty()) {
            int slimeCount = rand.nextInt(5) + 2;
            Collections.shuffle(fluidPositions, rand);
            for (int i = 0; i < Math.min(slimeCount, fluidPositions.size()); i++) {
                BlockPos spawnPos = fluidPositions.get(i);
                if (world.isAirBlock(spawnPos.up())) {
                    EntitySlime slime = new EntitySlime(world);
                    slime.setLocationAndAngles(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
                    world.spawnEntity(slime);
                }
            }
        }

        return true;
    }
}
