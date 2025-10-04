package slimeknights.tconstruct.smeltery;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.TinkerIntegration;
import slimeknights.tconstruct.common.CommonProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.BucketCastingRecipe;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.smeltery.PreferenceCastingRecipe;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.shared.block.BlockSlime;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.block.BlockChannel;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.block.BlockSeared;
import slimeknights.tconstruct.smeltery.block.BlockSearedFurnaceController;
import slimeknights.tconstruct.smeltery.block.BlockSearedGlass;
import slimeknights.tconstruct.smeltery.block.BlockSearedLadder;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab;
import slimeknights.tconstruct.smeltery.block.BlockSearedSlab2;
import slimeknights.tconstruct.smeltery.block.BlockSearedStairs;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryIO;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.block.BlockTinkerTankController;
import slimeknights.tconstruct.smeltery.item.CastCustom;
import slimeknights.tconstruct.smeltery.item.ItemChannel;
import slimeknights.tconstruct.smeltery.item.ItemTank;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;
import slimeknights.tconstruct.smeltery.tileentity.TileDrain;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;
import slimeknights.tconstruct.smeltery.tileentity.TileSearedFurnace;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.world.TinkerWorld;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Pulse(id = TinkerSmeltery.PulseId, description = "The smeltery and items needed for it")
public class TinkerSmeltery extends TinkerPulse {

  public static final String PulseId = "TinkerSmeltery";
  public static final Logger log = Util.getLogger(PulseId);

  @SidedProxy(clientSide = "slimeknights.tconstruct.smeltery.SmelteryClientProxy", serverSide = "slimeknights.tconstruct.common.CommonProxy")
  public static CommonProxy proxy;

  // Blocks
  public static BlockSeared searedBlock;
  public static BlockSmelteryController smelteryController;
  public static BlockTank searedTank;
  public static BlockFaucet faucet;
  public static BlockChannel channel;
  public static BlockCasting castingBlock;
  public static BlockSmelteryIO smelteryIO;
  public static BlockSearedGlass searedGlass;
  public static BlockSearedLadder searedLadder;

  public static Block searedFurnaceController;
  public static Block tinkerTankController;

  public static BlockSearedSlab searedSlab;
  public static BlockSearedSlab2 searedSlab2;

  // stairs
  public static Block searedStairsStone;
  public static Block searedStairsCobble;
  public static Block searedStairsPaver;
  public static Block searedStairsBrick;
  public static Block searedStairsBrickCracked;
  public static Block searedStairsBrickFancy;
  public static Block searedStairsBrickSquare;
  public static Block searedStairsBrickTriangle;
  public static Block searedStairsBrickSmall;
  public static Block searedStairsRoad;
  public static Block searedStairsTile;
  public static Block searedStairsCreeper;

  // Items
  public static Cast cast;
  public static CastCustom castCustom;
  public static Cast clayCast;

  // itemstacks!
  public static ItemStack castIngot;
  public static ItemStack castNugget;
  public static ItemStack castGem;
  public static ItemStack castShard;
  public static ItemStack castPlate;
  public static ItemStack castGear;

  private static Map<Fluid, Set<Pair<String, Integer>>> knownOreFluids = new Object2ObjectOpenHashMap<>();
  public static List<FluidStack> castCreationFluids = new ObjectArrayList<>();
  public static List<FluidStack> clayCreationFluids = new ObjectArrayList<>();

  public static ImmutableSet<Block> validSmelteryBlocks;
  public static ImmutableSet<Block> searedStairsSlabs;
  public static ImmutableSet<Block> validTinkerTankBlocks;
  public static ImmutableSet<Block> validTinkerTankFloorBlocks;
  public static List<ItemStack> meltingBlacklist = new ObjectArrayList<>();

  @SubscribeEvent
  public void registerBlocks(Register<Block> event) {
    IForgeRegistry<Block> registry = event.getRegistry();

    searedBlock = registerBlock(registry, new BlockSeared(), "seared");
    smelteryController = registerBlock(registry, new BlockSmelteryController(), "smeltery_controller");
    searedTank = registerBlock(registry, new BlockTank(), "seared_tank");
    faucet = registerBlock(registry, new BlockFaucet(), "faucet");
    channel = registerBlock(registry, new BlockChannel(), "channel");
    castingBlock = registerBlock(registry, new BlockCasting(), "casting");
    smelteryIO = registerBlock(registry, new BlockSmelteryIO(), "smeltery_io");
    searedGlass = registerBlock(registry, new BlockSearedGlass(), "seared_glass");
    searedLadder = registerBlock(registry, new BlockSearedLadder(), "seared_ladder");

    searedFurnaceController = registerBlock(registry, new BlockSearedFurnaceController(), "seared_furnace_controller");
    tinkerTankController = registerBlock(registry, new BlockTinkerTankController(), "tinker_tank_controller");

    // slabs
    searedSlab = registerBlock(registry, new BlockSearedSlab(), "seared_slab");
    searedSlab2 = registerBlock(registry, new BlockSearedSlab2(), "seared_slab2");

    // stairs
    searedStairsStone = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.STONE, "seared_stairs_stone");
    searedStairsCobble = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.COBBLE, "seared_stairs_cobble");
    searedStairsPaver = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.PAVER, "seared_stairs_paver");
    searedStairsBrick = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.BRICK, "seared_stairs_brick");
    searedStairsBrickCracked = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.BRICK_CRACKED, "seared_stairs_brick_cracked");
    searedStairsBrickFancy = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.BRICK_FANCY, "seared_stairs_brick_fancy");
    searedStairsBrickSquare = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.BRICK_SQUARE, "seared_stairs_brick_square");
    searedStairsBrickTriangle = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.BRICK_TRIANGLE, "seared_stairs_brick_triangle");
    searedStairsBrickSmall = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.BRICK_SMALL, "seared_stairs_brick_small");
    searedStairsRoad = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.ROAD, "seared_stairs_road");
    searedStairsTile = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.TILE, "seared_stairs_tile");
    searedStairsCreeper = registerBlockSearedStairsFrom(registry, searedBlock, BlockSeared.SearedType.CREEPER, "seared_stairs_creeper");

    registerTE(TileSmeltery.class, "smeltery_controller");
    registerTE(TileSmelteryComponent.class, "smeltery_component");
    registerTE(TileTank.class, "tank");
    registerTE(TileFaucet.class, "faucet");
    registerTE(TileChannel.class, "channel");
    registerTE(TileCastingTable.class, "casting_table");
    registerTE(TileCastingBasin.class, "casting_basin");
    registerTE(TileDrain.class, "smeltery_drain");
    registerTE(TileSearedFurnace.class, "seared_furnace");
    registerTE(TileTinkerTank.class, "tinker_tank");
  }

  @SubscribeEvent
  public void registerItems(Register<Item> event) {
    IForgeRegistry<Item> registry = event.getRegistry();

    searedBlock = registerEnumItemBlock(registry, searedBlock);
    smelteryController = registerItemBlock(registry, smelteryController);
    searedTank = registerItemBlockProp(registry, new ItemTank(searedTank), BlockTank.TYPE);
    faucet = registerItemBlock(registry, faucet);
    channel = registerItemBlock(registry, new ItemChannel(channel));
    castingBlock = registerItemBlockProp(registry, new ItemBlockMeta(castingBlock), BlockCasting.TYPE);
    smelteryIO = registerEnumItemBlock(registry, smelteryIO);
    searedGlass = registerEnumItemBlock(registry, searedGlass);
    searedLadder = registerEnumItemBlock(registry, searedLadder);

    searedFurnaceController = registerItemBlock(registry, searedFurnaceController);
    tinkerTankController = registerItemBlock(registry, tinkerTankController);

    // slabs
    searedSlab = registerEnumItemBlockSlab(registry, searedSlab);
    searedSlab2 = registerEnumItemBlockSlab(registry, searedSlab2);

    // stairs
    searedStairsStone = registerItemBlock(registry, searedStairsStone);
    searedStairsCobble = registerItemBlock(registry, searedStairsCobble);
    searedStairsPaver = registerItemBlock(registry, searedStairsPaver);
    searedStairsBrick = registerItemBlock(registry, searedStairsBrick);
    searedStairsBrickCracked = registerItemBlock(registry, searedStairsBrickCracked);
    searedStairsBrickFancy = registerItemBlock(registry, searedStairsBrickFancy);
    searedStairsBrickSquare = registerItemBlock(registry, searedStairsBrickSquare);
    searedStairsBrickTriangle = registerItemBlock(registry, searedStairsBrickTriangle);
    searedStairsBrickSmall = registerItemBlock(registry, searedStairsBrickSmall);
    searedStairsRoad = registerItemBlock(registry, searedStairsRoad);
    searedStairsTile = registerItemBlock(registry, searedStairsTile);
    searedStairsCreeper = registerItemBlock(registry, searedStairsCreeper);

    cast = registerItem(registry, new Cast(), "cast");
    castCustom = registerItem(registry, new CastCustom(), "cast_custom");
    castIngot = castCustom.addMeta(0, "ingot", Material.VALUE_Ingot);
    castNugget = castCustom.addMeta(1, "nugget", Material.VALUE_Nugget);
    castGem = castCustom.addMeta(2, "gem", Material.VALUE_Gem);
    castPlate = castCustom.addMeta(3, "plate", Material.VALUE_Ingot);
    castGear = castCustom.addMeta(4, "gear", Material.VALUE_Ingot * 4);

    if(Config.claycasts) {
      clayCast = registerItem(registry, new Cast(), "clay_cast");
    }

    if(TinkerRegistry.getShard() != null) {
      TinkerRegistry.addCastForItem(TinkerRegistry.getShard());
      castShard = new ItemStack(cast);
      Cast.setTagForPart(castShard, TinkerRegistry.getShard());
    }
    
    // smeltery blocks
    ImmutableSet.Builder<Block> builder = ImmutableSet.builder();
    builder.add(searedBlock);
    builder.add(searedTank);
    builder.add(smelteryIO);
    builder.add(searedGlass);
    builder.add(searedLadder);

    validSmelteryBlocks = builder.build();
    validTinkerTankBlocks = builder.build(); // same blocks right now
    validTinkerTankFloorBlocks = ImmutableSet.of(searedBlock, searedGlass, smelteryIO);

    // seared furnace ceiling blocks, no smelteryIO or seared glass
    // does not affect sides, those are forced to use seared blocks/tanks where relevant
    builder = ImmutableSet.builder();
    builder.add(searedBlock);

    builder.add(searedSlab);
    builder.add(searedSlab2);
    builder.add(searedStairsStone);
    builder.add(searedStairsCobble);
    builder.add(searedStairsPaver);
    builder.add(searedStairsBrick);
    builder.add(searedStairsBrickCracked);
    builder.add(searedStairsBrickFancy);
    builder.add(searedStairsBrickSquare);
    builder.add(searedStairsBrickTriangle);
    builder.add(searedStairsBrickSmall);
    builder.add(searedStairsRoad);
    builder.add(searedStairsTile);
    builder.add(searedStairsCreeper);

    searedStairsSlabs = builder.build();
  }

  @SubscribeEvent
  public void registerModels(ModelRegistryEvent event) {
    proxy.registerModels();
  }

  // PRE-INITIALIZATION
  @Subscribe
  public void preInit(FMLPreInitializationEvent event) {
    proxy.preInit();
  }

  // INITIALIZATION
  @Subscribe
  public void init(FMLInitializationEvent event) {
    // done here so they're present for integration in MaterialIntegration and fluids in TinkerFluids are also initialized
    castCreationFluids.add(new FluidStack(TinkerFluids.gold, Material.VALUE_Ingot * 2));

    // always add extra fluids, as we are not sure if they are integrated until the end of postInit and we added recipes using them before integration
    castCreationFluids.add(new FluidStack(TinkerFluids.brass, Material.VALUE_Ingot));
    castCreationFluids.add(new FluidStack(TinkerFluids.alubrass, Material.VALUE_Ingot));

    // add clay casts if enabled
    if(Config.claycasts) {
      clayCreationFluids.add(new FluidStack(TinkerFluids.clay, Material.VALUE_Ingot * 2));
    }

    registerSmelting();

    proxy.init();
  }

  private void registerSmelting() {
    GameRegistry.addSmelting(TinkerCommons.grout, TinkerCommons.searedBrick, 0.4f);

    GameRegistry.addSmelting(new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK.getMeta()), new ItemStack(searedBlock, 1, BlockSeared.SearedType.BRICK_CRACKED.getMeta()), 0.1f);
  }

  // POST-INITIALIZATION
  @Subscribe
  public void postInit(FMLPostInitializationEvent event) {
    registerSmelteryFuel();
    registerMeltingCasting();

    // register remaining cast creation
    for(FluidStack fs : castCreationFluids) {
      TinkerRegistry.registerTableCasting(new ItemStack(cast), ItemStack.EMPTY, fs.getFluid(), fs.amount);
      TinkerRegistry.registerTableCasting(new CastingRecipe(castGem, RecipeMatch.of("gemEmerald"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castGem, RecipeMatch.of("gemDiamond"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of("ingotBrick"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of("ingotBrickNether"), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, new RecipeMatch.Item(TinkerCommons.searedBrick, 1), fs, true, true));
    }

    proxy.postInit();
    TinkerRegistry.tabSmeltery.setDisplayIcon(new ItemStack(searedTank));
  }

  private void registerSmelteryFuel() {
    TinkerRegistry.registerSmelteryFuel(new FluidStack(FluidRegistry.LAVA, 50), 100);
    TinkerRegistry.registerSmelteryFuel(new FluidStack(TinkerFluids.blazingBlood, 50), 150);
  }

  private void registerMeltingCasting() {
    // used in several places to register fluids for the crafting recipe scan
    ImmutableSet.Builder<Pair<String, Integer>> builder;
    int bucket = Fluid.BUCKET_VOLUME;

    // bucket casting
    TinkerRegistry.registerTableCasting(new BucketCastingRecipe(Items.BUCKET));

    // Water
    Fluid water = FluidRegistry.WATER;
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.ICE, bucket), water, 305));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.PACKED_ICE, bucket * 2), water, 310));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Blocks.SNOW, bucket), water, 305));
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(Items.SNOWBALL, bucket / 8), water, 301));

    // bloooooood
    TinkerRegistry.registerMelting(Items.ROTTEN_FLESH, TinkerFluids.blood, 40);
    if(TinkerCommons.matSlimeBallBlood != null) {
      // blood slime
      TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerCommons.matSlimeBallBlood.copy(), null, TinkerFluids.blood, Material.VALUE_SlimeBall, 50));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.BLOOD.meta), null, TinkerFluids.blood, Material.VALUE_SlimeBall * 4, 100));

      TinkerRegistry.registerMelting(TinkerCommons.matSlimeBallBlood, TinkerFluids.blood, Material.VALUE_SlimeBall);
      ItemStack slimeblock_blood = new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.BLOOD.meta);
      TinkerRegistry.registerMelting(slimeblock_blood, TinkerFluids.blood, Material.VALUE_SlimeBall * 4);
    }
    
    // venom
    TinkerRegistry.registerMelting(Items.SPIDER_EYE, TinkerFluids.venom, 40);
    TinkerRegistry.registerMelting(new ItemStack(Items.FISH, 1, 3), TinkerFluids.venom, 40);
    
    // green slime
    TinkerRegistry.registerMelting(Items.SLIME_BALL, TinkerFluids.greenSlime, Material.VALUE_SlimeBall);
    ItemStack slimeblock_green = new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.GREEN.meta);
    TinkerRegistry.registerMelting(slimeblock_green, TinkerFluids.greenSlime, Material.VALUE_SlimeBall * 4);
    slimeblock_green = new ItemStack(Blocks.SLIME_BLOCK);
    TinkerRegistry.registerMelting(slimeblock_green, TinkerFluids.greenSlime, Material.VALUE_SlimeBall * 9);
    
    // blue slime
    TinkerRegistry.registerMelting(TinkerCommons.matSlimeBallBlue, TinkerFluids.blueslime, Material.VALUE_SlimeBall);
    ItemStack slimeblock_blue = new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.BLUE.meta);
    TinkerRegistry.registerMelting(slimeblock_blue, TinkerFluids.blueslime, Material.VALUE_SlimeBall * 4);
    slimeblock_blue = new ItemStack(TinkerCommons.blockSlime, 1, BlockSlime.SlimeType.BLUE.meta);
    TinkerRegistry.registerMelting(slimeblock_blue, TinkerFluids.blueslime, Material.VALUE_SlimeBall * 9);
    
    // purple slime
    TinkerRegistry.registerMelting(TinkerCommons.matSlimeBallPurple, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall);
    ItemStack slimeblock = new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.PURPLE.meta);
    TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 4);
    slimeblock = new ItemStack(TinkerCommons.blockSlime, 1, BlockSlime.SlimeType.PURPLE.meta);
    TinkerRegistry.registerMelting(slimeblock, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 9);

    // seared stone, takes as long as a full block to melt, but gives less
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("stone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore()));
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("cobblestone", Material.VALUE_SearedMaterial),
                                                           TinkerFluids.searedStone, Material.VALUE_Ore()));
  
    // seared stone, misc blocks
    TinkerRegistry.registerMelting(searedLadder, TinkerFluids.searedStone, 288);

    // obsidian
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of("obsidian", Material.VALUE_Ore()),
                                                           TinkerFluids.obsidian, Material.VALUE_Ore()));
    // note that obsidian casting gives you 2 ingot value per obsidian, while part crafting only gives 1 per obsidian
    registerToolpartMeltingCasting(TinkerMaterials.obsidian);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.OBSIDIAN), ItemStack.EMPTY, TinkerFluids.obsidian, Material.VALUE_Ore());

    // gold is integrated via MaterialIntegration in TinkerIntegration now

    // special melting
    TinkerRegistry.registerMelting(Items.IRON_HORSE_ARMOR, TinkerFluids.iron, Material.VALUE_Ingot * 4);
    TinkerRegistry.registerMelting(Items.GOLDEN_HORSE_ARMOR, TinkerFluids.gold, Material.VALUE_Ingot * 4);

    // rails, some of these are caught through registerOredictMelting, but for consistency all are just registered here
    TinkerRegistry.registerMelting(Blocks.RAIL, TinkerFluids.iron, Material.VALUE_Ingot * 6 / 16);
    TinkerRegistry.registerMelting(Blocks.ACTIVATOR_RAIL, TinkerFluids.iron, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(Blocks.DETECTOR_RAIL, TinkerFluids.iron, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(Blocks.GOLDEN_RAIL, TinkerFluids.gold, Material.VALUE_Ingot);

    // register stone toolpart melting
    for(IToolPart toolPart : TinkerRegistry.getToolParts()) {
      if(toolPart.canBeCasted()) {
        if(toolPart instanceof MaterialItem) {
          ItemStack stack = toolPart.getItemstackWithMaterial(TinkerMaterials.stone);
          TinkerRegistry.registerMelting(MeltingRecipe.forAmount(
              RecipeMatch.ofNBT(stack, (toolPart.getCost() * Material.VALUE_SearedMaterial) / Material.VALUE_Ingot),
              TinkerFluids.searedStone, (int)(toolPart.getCost() * Config.oreToIngotRatio)));
        }
      }
    }

    // seared block casting and melting
    ItemStack blockSeared = new ItemStack(searedBlock);
    blockSeared.setItemDamage(BlockSeared.SearedType.STONE.getMeta());
    TinkerRegistry.registerTableCasting(TinkerCommons.searedBrick, castIngot, TinkerFluids.searedStone, Material.VALUE_SearedMaterial);
    TinkerRegistry.registerBasinCasting(blockSeared, ItemStack.EMPTY, TinkerFluids.searedStone, Material.VALUE_SearedBlock);

    ItemStack searedCobble = new ItemStack(searedBlock, 1, BlockSeared.SearedType.COBBLE.getMeta());
    TinkerRegistry.registerBasinCasting(new CastingRecipe(searedCobble, RecipeMatch.of("cobblestone"), TinkerFluids.searedStone, Material.VALUE_SearedBlock - Material.VALUE_SearedMaterial, true, false));

    // seared furnaces have an additional recipe above using a crafting table, to allow creation without a smeltery
    // this one is convenience for those with one
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(searedFurnaceController),
                                                          RecipeMatch.of(Blocks.FURNACE),
                                                          new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial * 8),
                                                          true, true));

    // seared glass convenience recipe
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(searedGlass, 1, BlockSearedGlass.GlassType.GLASS.getMeta()),
                                                          RecipeMatch.of("blockGlass"),
                                                          new FluidStack(TinkerFluids.searedStone, Material.VALUE_SearedMaterial * 4),
                                                          true, true));

    // basically a pseudo-oredict of the seared blocks to support wildcard value
    TinkerRegistry.registerMelting(searedBlock, TinkerFluids.searedStone, Material.VALUE_SearedBlock);
    TinkerRegistry.registerMelting(TinkerCommons.searedBrick, TinkerFluids.searedStone, Material.VALUE_SearedMaterial);
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(RecipeMatch.of(TinkerCommons.grout, Material.VALUE_SearedMaterial), TinkerFluids.searedStone, Material.VALUE_SearedMaterial / 3));

    // melt all the dirt into mud
    ItemStack stack = new ItemStack(Blocks.DIRT, 1, OreDictionary.WILDCARD_VALUE);
    RecipeMatch rm = new RecipeMatch.Item(stack, 1, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(MeltingRecipe.forAmount(rm, TinkerFluids.dirt, Material.VALUE_BrickBlock));
    TinkerRegistry.registerTableCasting(TinkerCommons.mudBrick, castIngot, TinkerFluids.dirt, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(TinkerCommons.mudBrick, TinkerFluids.dirt, Material.VALUE_Ingot);
    TinkerRegistry.registerMelting(TinkerCommons.mudBrickBlock, TinkerFluids.dirt, Material.VALUE_BrickBlock);

    // hardened clay
    builder = ImmutableSet.builder();
    builder.add(Pair.of("clay", Material.VALUE_Ingot));
    builder.add(Pair.of("blockClay", Material.VALUE_BrickBlock));
    addKnownOreFluid(TinkerFluids.clay, builder.build());

    // decided against support for melting hardened clay. Once it's hardened, it stays hard. Same for bricks.
    //TinkerRegistry.registerMelting(Blocks.hardened_clay, TinkerFluids.clay, Material.VALUE_BrickBlock);
    //TinkerRegistry.registerMelting(Blocks.stained_hardened_clay, TinkerFluids.clay, Material.VALUE_BrickBlock);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.HARDENED_CLAY), ItemStack.EMPTY, TinkerFluids.clay, Material.VALUE_BrickBlock);
    // funny thing about hardened clay. If it's stained and you wash it with water, it turns back into regular hardened clay!
    TinkerRegistry.registerBasinCasting(new CastingRecipe(
                                                          new ItemStack(Blocks.HARDENED_CLAY),
                                                          RecipeMatch.of(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, OreDictionary.WILDCARD_VALUE)),
                                                          new FluidStack(FluidRegistry.WATER, 250),
                                                          150,
                                                          true,
                                                          false));
    // let's allow bricks because we're nice
    if(Config.castableBricks) {
      TinkerRegistry.registerTableCasting(new ItemStack(Items.BRICK), castIngot, TinkerFluids.clay, Material.VALUE_Ingot);
    }

    // emerald melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("oreEmerald", (int) (Material.VALUE_Gem * Config.oreToIngotRatio)), TinkerFluids.emerald));
    builder = ImmutableSet.builder();
    builder.add(Pair.of("gemEmerald", Material.VALUE_Gem));
    builder.add(Pair.of("blockEmerald", Material.VALUE_Gem * 9));
    addKnownOreFluid(TinkerFluids.emerald, builder.build());

    TinkerRegistry.registerTableCasting(new ItemStack(Items.EMERALD), castGem, TinkerFluids.emerald, Material.VALUE_Gem);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.EMERALD_BLOCK), ItemStack.EMPTY, TinkerFluids.emerald, Material.VALUE_Gem * 9);

    // diamond melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("oreDiamond", (int) (Material.VALUE_Gem * Config.oreToIngotRatio)), TinkerFluids.diamond));
    builder = ImmutableSet.builder();
    builder.add(Pair.of("gemDiamond", Material.VALUE_Gem));
    builder.add(Pair.of("blockDiamond", Material.VALUE_Gem * 9));
    addKnownOreFluid(TinkerFluids.diamond, builder.build());

    TinkerRegistry.registerTableCasting(new ItemStack(Items.DIAMOND), castGem, TinkerFluids.diamond, Material.VALUE_Gem);
    TinkerRegistry.registerBasinCasting(new ItemStack(Blocks.DIAMOND_BLOCK), ItemStack.EMPTY, TinkerFluids.diamond, Material.VALUE_Gem * 9);

    // glass melting and casting
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("sand", Material.VALUE_Glass), TinkerFluids.glass));
    builder = ImmutableSet.builder();
    builder.add(Pair.of("blockGlass", Material.VALUE_Glass));
    builder.add(Pair.of("paneGlass", Material.VALUE_Glass * 6 / 16));
    addKnownOreFluid(TinkerFluids.glass, builder.build());

    TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Blocks.GLASS_PANE), null, TinkerFluids.glass, Material.VALUE_Glass * 6 / 16, 50));
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockClearGlass), null, TinkerFluids.glass, Material.VALUE_Glass, 120));
    
    // bone melting and casting
    TinkerRegistry.registerMelting(new ItemStack(Items.DYE, 1, 15), TinkerFluids.calcium, Material.VALUE_Ingot / 3);
    TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of("bone", Material.VALUE_Ingot), TinkerFluids.calcium));
    TinkerRegistry.registerMelting(new ItemStack(Blocks.BONE_BLOCK), TinkerFluids.calcium, Material.VALUE_Ingot * 3);
    
    TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Items.BONE), null, TinkerFluids.calcium, Material.VALUE_Ingot, 50));
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(Blocks.BONE_BLOCK), null, TinkerFluids.calcium, Material.VALUE_Ingot * 3, 100));

    // lavawood
    TinkerRegistry.registerBasinCasting(new CastingRecipe(TinkerCommons.lavawood, RecipeMatch.of("plankWood"),
                                                          new FluidStack(FluidRegistry.LAVA, 250),
                                                          100, true, false));

    // red sand
    TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(Blocks.SAND, 1, 1),
                                                          RecipeMatch.of(new ItemStack(Blocks.SAND, 1, 0)),
                                                          new FluidStack(TinkerFluids.blood, 10),
                                                          true, false));
    
    // slime casting
    if(isWorldLoaded()) {
      TinkerRegistry.registerTableCasting(new CastingRecipe(new ItemStack(Items.SLIME_BALL), null, TinkerFluids.greenSlime, Material.VALUE_SlimeBall, 50));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerWorld.slimeDirt, 1, BlockSlime.SlimeType.GREEN.meta), RecipeMatch.of("dirt"), new FluidStack(TinkerFluids.greenSlime, Material.VALUE_SlimeBall), 100, true, false));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.GREEN.meta), null, TinkerFluids.greenSlime, Material.VALUE_SlimeBall * 4, 100));
      
      TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerCommons.matSlimeBallBlue.copy(), null, TinkerFluids.blueslime, Material.VALUE_SlimeBall, 50));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerWorld.slimeDirt, 1, BlockSlime.SlimeType.BLUE.meta), RecipeMatch.of("dirt"), new FluidStack(TinkerFluids.blueslime, Material.VALUE_SlimeBall), 100, true, false));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.BLUE.meta), null, TinkerFluids.blueslime, Material.VALUE_SlimeBall * 4, 100));
      
      TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerCommons.matSlimeBallPurple.copy(), null, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall, 50));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerWorld.slimeDirt, 1, BlockSlime.SlimeType.PURPLE.meta), RecipeMatch.of("dirt"), new FluidStack(TinkerFluids.purpleSlime, Material.VALUE_SlimeBall), 100, true, false));
      TinkerRegistry.registerBasinCasting(new CastingRecipe(new ItemStack(TinkerCommons.blockSlimeCongealed, 1, BlockSlime.SlimeType.PURPLE.meta), null, TinkerFluids.purpleSlime, Material.VALUE_SlimeBall * 4, 100));
    }
    
    // bone casting
    if(TinkerCommons.isToolsLoaded()) {
      TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerCommons.matBloodyBone, RecipeMatch.of("bone"),
              new FluidStack(TinkerFluids.blood, Material.VALUE_SlimeBall), true, false));
      
      TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerCommons.matVenomousBone, RecipeMatch.of("bone"),
              new FluidStack(TinkerFluids.venom, Material.VALUE_SlimeBall), true, false));
      
      TinkerRegistry.registerTableCasting(new CastingRecipe(TinkerCommons.matBlazingBone, RecipeMatch.of("boneWithered"),
              new FluidStack(TinkerFluids.blazingBlood, 250), 12, true, false));
    }

    // melt entities into a pulp
    TinkerRegistry.registerEntityMelting();
  }

  /**
   * Called by Tinkers Integration to register allows, some are conditional on integrations being loaded
   */
  public static void registerAlloys() {
    if(!isSmelteryLoaded()) {
      return;
    }

    // 1 bucket lava + 1 bucket water = 2 ingots = 1 block obsidian
    // 1000 + 1000 = 288
    // 125 + 125 = 36
    if(Config.obsidianAlloy) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.obsidian, 36),
                                   new FluidStack(FluidRegistry.WATER, 125),
                                   new FluidStack(FluidRegistry.LAVA, 125));
    }

    // 1 bucket water + 4 seared ingot + 4 mud bricks = 1 block hardened clay
    // 1000 + 288 + 576 = 576
    // 250 + 72 + 144 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.clay, 144),
                                 new FluidStack(FluidRegistry.WATER, 250),
                                 new FluidStack(TinkerFluids.searedStone, 72),
                                 new FluidStack(TinkerFluids.dirt, 144));

    // 1 iron ingot + 1 purple slime ball + seared stone in molten form = 1 knightslime ingot
    // 144 + 250 + 288 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.knightslime, 72),
                                 new FluidStack(TinkerFluids.iron, 72),
                                 new FluidStack(TinkerFluids.purpleSlime, 125),
                                 new FluidStack(TinkerFluids.searedStone, 144));

    // i iron ingot + 1 blood... unit thingie + 1/3 gem = 1 pigiron
    // 144 + 99 + 222 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.pigIron, 144),
                                 new FluidStack(TinkerFluids.iron, 144),
                                 new FluidStack(TinkerFluids.blood, 40),
                                 new FluidStack(TinkerFluids.clay, 72));

    // 2 ingot cobalt + 2 ingot ardite = 2 ingot manyullyn!
    // 144 + 144 = 144
    TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.manyullyn, 2),
                                 new FluidStack(TinkerFluids.cobalt, 2),
                                 new FluidStack(TinkerFluids.ardite, 2));

    // 3 ingots copper + 1 ingot tin = 4 ingots bronze
    if(TinkerIntegration.isIntegrated(TinkerFluids.bronze) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.tin)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.bronze, 4),
                                   new FluidStack(TinkerFluids.copper, 3),
                                   new FluidStack(TinkerFluids.tin, 1));
    }

    // 1 ingot gold + 1 ingot silver = 2 ingots electrum
    if(TinkerIntegration.isIntegrated(TinkerFluids.electrum) &&
       TinkerIntegration.isIntegrated(TinkerFluids.gold) &&
       TinkerIntegration.isIntegrated(TinkerFluids.silver)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.electrum, 2),
                                   new FluidStack(TinkerFluids.gold, 1),
                                   new FluidStack(TinkerFluids.silver, 1));
    }

    // 1 ingot copper + 3 ingots aluminium = 4 ingots alubrass
    if(TinkerIntegration.isIntegrated(TinkerFluids.alubrass) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.aluminum)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.alubrass, 4),
                                   new FluidStack(TinkerFluids.copper, 1),
                                   new FluidStack(TinkerFluids.aluminum, 3));
    }

    // 2 ingots copper + 1 ingot zinc = 3 ingots brass
    if(TinkerIntegration.isIntegrated(TinkerFluids.brass) &&
       TinkerIntegration.isIntegrated(TinkerFluids.copper) &&
       TinkerIntegration.isIntegrated(TinkerFluids.zinc)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.brass, 3),
                                   new FluidStack(TinkerFluids.copper, 2),
                                   new FluidStack(TinkerFluids.zinc, 1));
    }

    // 1 ingot aluminum + 1 ingot iron + 1 obsidian = 1 alumite
    // 144 + 144 + 288 = 144
    if(TinkerIntegration.isIntegrated(TinkerFluids.alumite) &&
       TinkerIntegration.isIntegrated(TinkerFluids.aluminum) &&
       TinkerIntegration.isIntegrated(TinkerFluids.iron)) {
      TinkerRegistry.registerAlloy(new FluidStack(TinkerFluids.alumite, 144),
                                   new FluidStack(TinkerFluids.aluminum, 144),
                                   new FluidStack(TinkerFluids.iron, 144),
                                   new FluidStack(TinkerFluids.obsidian, 288));
    }
  }

  /**
   * Called by MaterialIntegration's to register tool part recipes
   * @param material
   */
  public static void registerToolpartMeltingCasting(Material material) {
    // melt ALL the toolparts n stuff. Also cast them.
    Fluid fluid = material.getFluid();
    for(IToolPart toolPart : TinkerRegistry.getToolParts()) {
      if(!toolPart.canBeCasted()) {
        continue;
      }
      if(!toolPart.canUseMaterial(material)) {
        continue;
      }
      if(toolPart instanceof MaterialItem) {
        ItemStack stack = toolPart.getItemstackWithMaterial(material);
        ItemStack cast = new ItemStack(TinkerSmeltery.cast);
        Cast.setTagForPart(cast, stack.getItem());

        if(fluid != null) {
          // melting
          TinkerRegistry.registerMelting(stack, fluid, toolPart.getCost());
          // casting
          TinkerRegistry.registerTableCasting(stack, cast, fluid, toolPart.getCost());
        }
        // register cast creation from the toolparts
        for(FluidStack fs : castCreationFluids) {
          TinkerRegistry.registerTableCasting(new CastingRecipe(cast,
                                                                RecipeMatch.ofNBT(stack),
                                                                fs,
                                                                true, true));
        }

        // clay casts
        if(Config.claycasts) {
          ItemStack clayCast = new ItemStack(TinkerSmeltery.clayCast);
          Cast.setTagForPart(clayCast, stack.getItem());

          if(fluid != null) {
            RecipeMatch rm = RecipeMatch.ofNBT(clayCast);
            FluidStack fs = new FluidStack(fluid, toolPart.getCost());
            TinkerRegistry.registerTableCasting(new CastingRecipe(stack, rm, fs, true, false));
          }
          for(FluidStack fs : clayCreationFluids) {
            TinkerRegistry.registerTableCasting(new CastingRecipe(clayCast,
                                                                  RecipeMatch.ofNBT(stack),
                                                                  fs,
                                                                  true, true));
          }
        }
      }
    }

    // same for shard
    if(castShard != null) {
      ItemStack stack = TinkerRegistry.getShard(material);
      int cost = TinkerRegistry.getShard().getCost();

      if(fluid != null) {
        // melting
        TinkerRegistry.registerMelting(stack, fluid, cost);
        // casting
        TinkerRegistry.registerTableCasting(stack, castShard, fluid, cost);
      }
      // register cast creation from the toolparts
      for(FluidStack fs : castCreationFluids) {
        TinkerRegistry.registerTableCasting(new CastingRecipe(castShard,
                                                              RecipeMatch.ofNBT(stack),
                                                              fs,
                                                              true, true));
      }
    }
  }

  /**
   * Registers melting for all directly supported pre- and suffixes of the ore.
   * E.g. "Iron" -> "ingotIron", "blockIron", "oreIron",
   */
  @SuppressWarnings("unchecked")
  public static void registerOredictMeltingCasting(Fluid fluid, String ore) {
    ImmutableSet.Builder<Pair<String, Integer>> builder = ImmutableSet.builder();
    Pair<String, Integer> nuggetOre = Pair.of("nugget" + ore, Material.VALUE_Nugget);
    Pair<String, Integer> ingotOre = Pair.of("ingot" + ore, Material.VALUE_Ingot);
    Pair<String, Integer> blockOre = Pair.of("block" + ore, Material.VALUE_Block);
    Pair<String, Integer> oreOre = Pair.of("ore" + ore, Material.VALUE_Ore());
    Pair<String, Integer> oreNetherOre = Pair.of("oreNether" + ore, (int) (2 * Material.VALUE_Ingot * Config.oreToIngotRatio));
    Pair<String, Integer> oreDenseOre = Pair.of("denseore" + ore, (int) (3 * Material.VALUE_Ingot * Config.oreToIngotRatio));
    Pair<String, Integer> orePoorOre = Pair.of("orePoor" + ore, (int) (Material.VALUE_Nugget * 3 * Config.oreToIngotRatio));
    Pair<String, Integer> oreNuggetOre = Pair.of("oreNugget" + ore, (int) (Material.VALUE_Nugget * Config.oreToIngotRatio));
    Pair<String, Integer> plateOre = Pair.of("plate" + ore, Material.VALUE_Ingot);
    Pair<String, Integer> gearOre = Pair.of("gear" + ore, Material.VALUE_Ingot * 4);
    Pair<String, Integer> dustOre = Pair.of("dust" + ore, Material.VALUE_Ingot);

    builder.add(nuggetOre, ingotOre, blockOre, oreOre, oreNetherOre, oreDenseOre, orePoorOre, oreNuggetOre, plateOre, gearOre, dustOre);
    Set<Pair<String, Integer>> knownOres = builder.build();

    // register oredicts
    addKnownOreFluid(fluid, knownOres);

    // register oredict castings!
    // ingot casting
    TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(ingotOre.getLeft(),
                                                                    RecipeMatch.ofNBT(castIngot),
                                                                    fluid,
                                                                    ingotOre.getRight()));
    // nugget casting
    TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(nuggetOre.getLeft(),
                                                                    RecipeMatch.ofNBT(castNugget),
                                                                    fluid,
                                                                    nuggetOre.getRight()));
    // block casting
    TinkerRegistry.registerBasinCasting(new PreferenceCastingRecipe(blockOre.getLeft(),
                                                                    null, // no cast
                                                                    fluid,
                                                                    blockOre.getRight()));
    // plate casting
    TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(plateOre.getLeft(),
                                                                    RecipeMatch.ofNBT(castPlate),
                                                                    fluid,
                                                                    plateOre.getRight()));
    // gear casting
    TinkerRegistry.registerTableCasting(new PreferenceCastingRecipe(gearOre.getLeft(),
                                                                    RecipeMatch.ofNBT(castGear),
                                                                    fluid,
                                                                    gearOre.getRight()));

    // and also cast creation!
    for(FluidStack fs : castCreationFluids) {
      TinkerRegistry.registerTableCasting(new CastingRecipe(castIngot, RecipeMatch.of(ingotOre.getLeft()), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castNugget, RecipeMatch.of(nuggetOre.getLeft()), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castPlate, RecipeMatch.of(plateOre.getLeft()), fs, true, true));
      TinkerRegistry.registerTableCasting(new CastingRecipe(castGear, RecipeMatch.of(gearOre.getLeft()), fs, true, true));
    }
  }

  /**
   * Adds a fluid to the knownOreFluids list, adding recipes for each combination
   * @param fluid      Fluid recipes belong to
   * @param knownOres  Set of pairs of an oredict name to a integer fluid amount
   */
  private static void addKnownOreFluid(Fluid fluid, Set<Pair<String, Integer>> knownOres) {
    if(Arrays.stream(Config.fluidIgnore).anyMatch(f -> f.equals(fluid.getName()))) {
      return;
    }
    for(Pair<String, Integer> pair : knownOres) {
      TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(pair.getLeft(), pair.getRight()), fluid));
    }

    knownOreFluids.put(fluid, knownOres);
  }

  /**
   * take all fluids we registered oredicts for and scan all recipes for oredict recipes that we can apply this to
   * <p>
   * called in TinkerIntegration
   */
  public static void registerRecipeOredictMelting() {
    if(!isSmelteryLoaded()) {
      return;
    }

    log.info("Started adding oredict melting recipes");
    long start = System.nanoTime();

    // set up cache
    File cacheFile = new File("config" + File.separator + TConstruct.modID + "_oredictcache.dat");
    NBTTagCompound cacheNBT = new NBTTagCompound();

    if(cacheFile.exists()) {
      try {
        // check mod list
        cacheNBT = CompressedStreamTools.read(cacheFile);
        NBTTagList modsNBT = cacheNBT.getTagList("Mods", Constants.NBT.TAG_STRING);
        Set<String> cachedMods = StreamSupport.stream(modsNBT.spliterator(), false)
                .map(nbt -> ((NBTTagString) nbt).getString())
                .collect(Collectors.toSet());
        Set<String> currentMods = new HashSet<>(Loader.instance().getIndexedModList().keySet());
        if(!cachedMods.equals(currentMods)) {
          log.info("Mod list changed, rescanning recipes");
          cacheNBT = new NBTTagCompound();
        } else {
          // load cached recipes
          NBTTagList recipeList = cacheNBT.getTagList("Recipes", Constants.NBT.TAG_COMPOUND);
          for(int i = 0; i < recipeList.tagCount(); i++) {
            NBTTagCompound compound = recipeList.getCompoundTagAt(i);
            ItemStack item = new ItemStack(compound.getCompoundTag("item"));
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("fluid"));
            if(!item.isEmpty() && fluid != null) {
              TinkerRegistry.registerMelting(new MeltingRecipe(RecipeMatch.of(item, fluid.amount), fluid));
            }
          }
          log.info("Loaded cached oredict melting recipes in {} seconds", (System.nanoTime() - start) / 1000000000D);
          return;
        }
      } catch(IOException e) {
        log.error("Failed to read cache file, rescanning recipes", e);
      }
    } else {
      log.info("Cache file doesn't exist, scanning recipes");
    }

    // parse the ignore list from the config
    RecipeMatchRegistry oredictMeltingIgnore = new RecipeMatchRegistry();
    for(String ignore : Config.oredictMeltingIgnore) {
      // skip comments and empty lines
      if(ignore.isEmpty() || ignore.startsWith("#")) {
        continue;
      }

      // if it has a colon, assume item stack
      if(ignore.contains(":")) {
        String[] parts = ignore.split(":");
        int meta = OreDictionary.WILDCARD_VALUE;

        // try parsing meta if given
        if(parts.length > 2) {
          try {
            meta = Integer.parseInt(parts[2]);
            if(meta < 0) {
              log.error("Invalid oredict melting ignore {}, metadata must be non-negative", ignore);
              continue;
            }
          } catch(NumberFormatException e) {
            log.error("Invalid oredict melting ignore {}, metadata must be a number", ignore);
            continue;
          }
        }

        // find the item
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if(item == null || item == Items.AIR) {
          log.error("Invalid oredict melting ignore {}, unknown item", ignore);
          continue;
        }

        // add the override
        oredictMeltingIgnore.addItem(new ItemStack(item, 1, meta), 1, 1);
      } else {
        oredictMeltingIgnore.addItem(ignore);
      }
    }

    // precompute blacklist
    ObjectOpenHashSet<ItemStack> blacklistSet = new ObjectOpenHashSet<>(meltingBlacklist);

    // precompute ore fluid lookup with wildcard expansion
    Object2ObjectOpenHashMap<ItemStack, Pair<Fluid, Integer>> oreFluidLookup = new Object2ObjectOpenHashMap<>();
    NonNullList<ItemStack> subItems = NonNullList.create();
    for(Map.Entry<Fluid, Set<Pair<String, Integer>>> entry : knownOreFluids.entrySet()) {
      Fluid fluid = entry.getKey();
      for(Pair<String, Integer> pair : entry.getValue()) {
        for(ItemStack stack : OreDictionary.getOres(pair.getLeft(), false)) {
          if(stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            subItems.clear();
            stack.getItem().getSubItems(CreativeTabs.SEARCH, subItems);
            for(ItemStack subStack : subItems) {
              oreFluidLookup.put(subStack.copy(), Pair.of(fluid, pair.getRight()));
            }
          } else {
            oreFluidLookup.put(stack.copy(), Pair.of(fluid, pair.getRight()));
          }
        }
      }
    }

    // process recipes
    NBTTagList recipeList = new NBTTagList();
    for(IRecipe recipe : CraftingManager.REGISTRY) {
      ItemStack output = recipe.getRecipeOutput();
      if(output.isEmpty() || TinkerRegistry.getMelting(output) != null) continue;
      if(blacklistSet.stream().anyMatch(blacklist -> OreDictionary.itemMatches(blacklist, output, false))) continue;

      NonNullList<Ingredient> inputs = recipe.getIngredients();
      Object2IntOpenHashMap<Fluid> knownFluids = new Object2IntOpenHashMap<>(2);

      for(Ingredient ingredient : inputs) {
        ItemStack[] matchingStacks = ingredient.getMatchingStacks();
        if(matchingStacks.length == 0) continue;

        boolean found = false;
        for(ItemStack stack : matchingStacks) {
          if(oredictMeltingIgnore.matches(stack).isPresent()) {
            found = true;
            break;
          }
          Pair<Fluid, Integer> fluidData = null;
          for(ItemStack oreStack : oreFluidLookup.keySet()) {
            if(ingredientMatches(ingredient, oreStack)) {
              fluidData = oreFluidLookup.get(oreStack);
              break;
            }
          }
          if(fluidData != null) {
            knownFluids.addTo(fluidData.getLeft(), fluidData.getRight());
            found = true;
            break;
          }
        }
        if(!found) {
          knownFluids.clear();
          break;
        }
      }

      if(knownFluids.size() == 1) {
        Fluid fluid = knownFluids.keySet().iterator().next();
        int amount = knownFluids.getInt(fluid) / output.getCount();
        if(amount > 0) {
          ItemStack outputCopy = output.copy();
          outputCopy.setCount(1);
          MeltingRecipe meltingRecipe = new MeltingRecipe(RecipeMatch.of(outputCopy, amount), fluid);

          // register and cache
          TinkerRegistry.registerMelting(meltingRecipe);
          ItemStack input = meltingRecipe.input.getInputs().get(0);
          FluidStack outputFluid = meltingRecipe.output;
          if(!input.isEmpty() && FluidRegistry.isFluidRegistered(outputFluid.getFluid())) {
            NBTTagCompound recipeNBT = new NBTTagCompound();
            recipeNBT.setTag("item", input.serializeNBT());
            recipeNBT.setTag("fluid", fluidToNBT(outputFluid));
            recipeList.appendTag(recipeNBT);
          }
          log.trace("Added automatic melting recipe for {} ({} {})", outputCopy, amount, fluid.getName());
        }
      }
    }

    // write cache with mod list
    cacheNBT.setTag("Recipes", recipeList);
    NBTTagList modsNBT = new NBTTagList();
    Loader.instance().getIndexedModList().keySet().stream()
            .map(NBTTagString::new)
            .forEach(modsNBT::appendTag);
    cacheNBT.setTag("Mods", modsNBT);
    try {
      if(!cacheFile.exists()) {
        cacheFile.createNewFile();
      }
      CompressedStreamTools.write(cacheNBT, cacheFile);
      log.info("Cache file written successfully");
    } catch(IOException e) {
      log.error("Failed to write cache file", e);
    }
    // how fast were we?
    log.info("Oredict melting recipes finished in {} seconds", (System.nanoTime() - start) / 1000000000D);
  }

  /**
   * Ingredients do not handle the passed in item stack having wildcard metadata, so handle using getSubItems
   */
  private static boolean ingredientMatches(Ingredient ingredient, ItemStack stack) {
    if (stack.getMetadata() != OreDictionary.WILDCARD_VALUE) {
      return ingredient.apply(stack);
    }
    NonNullList<ItemStack> stacks = NonNullList.create();
    stack.getItem().getSubItems(CreativeTabs.SEARCH, stacks);
    return stacks.stream().anyMatch(ingredient::apply);
  }

  private static NBTTagCompound fluidToNBT(FluidStack fluidStack) {
    NBTTagCompound nbt = new NBTTagCompound();
    nbt.setString("FluidName", fluidStack.getFluid().getName());
    nbt.setInteger("Amount", fluidStack.amount);
    if(fluidStack.tag != null) {
      nbt.setTag("Tag", fluidStack.tag);
    }
    return nbt;
  }

  protected static <E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> BlockSearedStairs registerBlockSearedStairsFrom(IForgeRegistry<Block> registry, EnumBlock<E> block, E value, String name) {
    return registerBlock(registry, new BlockSearedStairs(block.getDefaultState().withProperty(block.prop, value)), name);
  }
}
