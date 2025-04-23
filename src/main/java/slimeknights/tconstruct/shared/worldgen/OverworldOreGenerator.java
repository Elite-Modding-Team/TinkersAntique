package slimeknights.tconstruct.shared.worldgen;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockCommonOre;

import java.util.Random;

public class OverworldOreGenerator implements IWorldGenerator {

    public static OverworldOreGenerator INSTANCE = new OverworldOreGenerator();

    public WorldGenMinable copperGen;
    public WorldGenMinable tinGen;
    public WorldGenMinable aluminumGen;

    public OverworldOreGenerator() {
        copperGen = new WorldGenMinable(TinkerCommons.blockCommonOre.getStateFromMeta(BlockCommonOre.OreTypes.COPPER.getMeta()), 5, BlockMatcher.forBlock(Blocks.STONE));
        tinGen = new WorldGenMinable(TinkerCommons.blockCommonOre.getStateFromMeta(BlockCommonOre.OreTypes.TIN.getMeta()), 5, BlockMatcher.forBlock(Blocks.STONE));
        aluminumGen = new WorldGenMinable(TinkerCommons.blockCommonOre.getStateFromMeta(BlockCommonOre.OreTypes.ALUMINUM.getMeta()), 5, BlockMatcher.forBlock(Blocks.STONE));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(world.provider instanceof WorldProviderSurface) {
            if(Config.genCopper) {
                generateOverworldOre(copperGen, Config.copperRate, Config.copperHeightMin, Config.copperHeightMax, random, chunkX, chunkZ, world);
            }
            if(Config.genTin) {
                generateOverworldOre(tinGen, Config.tinRate, Config.tinHeightMin, Config.tinHeightMax, random, chunkX, chunkZ, world);
            }
            if(Config.genAluminum) {
                generateOverworldOre(aluminumGen, Config.aluminumRate, Config.aluminumHeightMin, Config.aluminumHeightMax, random, chunkX, chunkZ, world);
            }
        }
    }

    public void generateOverworldOre(WorldGenMinable gen, int rate, int heightMin, int heightMax, Random random, int chunkX, int chunkZ, World world) {
        BlockPos pos;
        for(int i = 0; i < rate; i++) {
            pos = new BlockPos(chunkX * 16, heightMin, chunkZ * 16);
            pos = pos.add(random.nextInt(16), random.nextInt(heightMax - heightMin), random.nextInt(16));
            gen.generate(world, random, pos);
        }
    }
}
