package slimeknights.tconstruct.tools;

import net.minecraft.entity.monster.EntitySlime;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.tools.traits.*;
import slimeknights.tconstruct.world.entity.EntityBlueSlime;

public class TinkerTraits {

  // general material traits
  public static final AbstractTrait alien = new TraitAlien();
  public static final AbstractTrait aquadynamic = new TraitAquadynamic();
  public static final AbstractTrait aridiculous = new TraitAridiculous();
  public static final AbstractTrait autosmelt = new TraitAutosmelt();
  public static final AbstractTrait baconlicious = new TraitBaconlicious();
  public static final AbstractTrait cheap = new TraitCheap();
  public static final AbstractTrait cheapskate = new TraitCheapskate();
  public static final AbstractTrait coldblooded = new TraitColdblooded();
  public static final AbstractTrait crude = new TraitCrude(1);
  public static final AbstractTrait crude2 = new TraitCrude(2);
  public static final AbstractTrait crumbling = new TraitCrumbling();
  public static final AbstractTrait dense = new TraitDense();
  public static final AbstractTrait depthdigger = new TraitDepthdigger();
  public static final AbstractTrait duritos = new TraitDuritos(); // yes you read that correctly
  public static final AbstractTrait ecological = new TraitEcological();
  public static final AbstractTrait enderference = new TraitEnderference();
  public static final AbstractTrait established = new TraitEstablished();
  public static final AbstractTrait flammable = new TraitFlammable();
  public static final AbstractTrait fractured = new TraitBonusDamage("fractured", 1.5f);
  public static final AbstractTrait heavy = new TraitHeavy();
  public static final AbstractTrait hellish = new TraitHellish();
  public static final AbstractTrait holy = new TraitHoly();
  public static final AbstractTrait insatiable = new TraitInsatiable();
  public static final AbstractTrait jagged = new TraitJagged();
  public static final AbstractTrait lightweight = new TraitLightweight();
  public static final AbstractTrait magnetic = new TraitMagnetic(1);
  public static final AbstractTrait magnetic2 = new TraitMagnetic(2);
  public static final AbstractTrait momentum = new TraitMomentum();
  public static final AbstractTrait petramor = new TraitPetramor();
  public static final AbstractTrait poisonous = new TraitPoisonous();
  public static final AbstractTrait prickly = new TraitPrickly();
  public static final AbstractTrait raging = new TraitRaging(1);
  public static final AbstractTrait raging2 = new TraitRaging(2);
  public static final AbstractTrait sharp = new TraitSharp();
  public static final AbstractTrait shocking = new TraitShocking();
  public static final AbstractTrait slimeyGreen = new TraitSlimey("green", EntitySlime.class);
  public static final AbstractTrait slimeyBlue = new TraitSlimey("blue", EntityBlueSlime.class);
  public static final AbstractTrait spiky = new TraitSpiky();
  public static final AbstractTrait splintering = new TraitSplintering();
  public static final AbstractTrait splinters = new TraitSplinters();
  public static final AbstractTrait squeaky = new TraitSqueaky();
  public static final AbstractTrait superheat = new TraitSuperheat();
  public static final AbstractTrait stiff = new TraitStiff();
  public static final AbstractTrait stonebound = new TraitStonebound();
  public static final AbstractTrait tasty = new TraitTasty();
  public static final AbstractTrait unnatural = new TraitUnnatural();
  public static final AbstractTrait writable = new TraitWritable(1);
  public static final AbstractTrait writable2 = new TraitWritable(2);

  // arrow shaft traits
  public static final AbstractTrait breakable = new TraitBreakable();
  public static final AbstractTrait endspeed = new TraitEndspeed();
  public static final AbstractTrait freezing = new TraitFreezing();
  public static final AbstractTrait hovering = new TraitHovering();
  public static final AbstractTrait splitting = new TraitSplitting();
}
