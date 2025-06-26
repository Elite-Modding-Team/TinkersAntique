package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.ISound;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.sound.SoundFading;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.sound.ISoundSource;
import slimeknights.tconstruct.library.sound.SoundType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockFaucet;
import slimeknights.tconstruct.smeltery.network.FaucetActivationPacket;

public class TileFaucet extends TileEntity implements ITickable, ISoundSource {

  public static final int LIQUID_TRANSFER = Config.liquidTransferRate;
  public static final int TRANSACTION_AMOUNT = Material.VALUE_Ingot;

  // direction we're pulling liquid from. cached so we don't have to query the world every time. set on pour-begin
  public EnumFacing direction; // used to continue draining and for rendering
  public boolean isPouring;
  public boolean stopPouring;
  public FluidStack drained; // fluid is drained instantly and distributed over time. how much is left
  public boolean lastRedstoneState;

  public TileFaucet() {
    reset();
  }

  // begin pouring
  public boolean activate() {
    IBlockState state = getWorld().getBlockState(pos);
    // invalid state
    if(!state.getPropertyKeys().contains(BlockFaucet.FACING)) {
      return false;
    }

    // already pouring? we want to stop then
    if(isPouring) {
      stopPouring = true;
      return true;
    }

    direction = getWorld().getBlockState(pos).getValue(BlockFaucet.FACING);
    doTransfer();

    if(drained != null) {
      if (!world.isRemote) {
        getWorld().playSound(null, pos, Sounds.faucet_trigger, SoundCategory.BLOCKS, 1.0F, 0.8F + 0.4F * world.rand.nextFloat());
      }
      else {
        TinkerSmeltery.proxy.playSound(getSound());
      }
    }

    return isPouring;
  }

  public void handleRedstone(boolean hasSignal) {
    if(hasSignal != lastRedstoneState) {
      lastRedstoneState = hasSignal;
      if(hasSignal) {
        world.scheduleUpdate(pos, this.getBlockType(), 2);
      }
    }
  }

  @Override
  public void update() {
    if(getWorld().isRemote) {
      return;
    }
    // nothing to do if not pouring
    if(!isPouring) {
      return;
    }

    if(drained != null) {
      // done draining
      if(drained.amount <= 0) {
        drained = null;
        // pour me another, if we want to.
        if(!stopPouring) {
          doTransfer();
        }
        else {
          reset();
        }
        if (!getWorld().isRemote && !isPouring) {
          getWorld().playSound(null, pos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F, 0.8F + 0.4F * world.rand.nextFloat());
        }
      }
      else {
        // reduce amount (cooldown)
        pour();
      }
    }
  }

  protected void doTransfer() {
    // still got content left
    if(drained != null) {
      return;
    }
    IFluidHandler toDrain = getFluidHandler(pos.offset(direction), direction.getOpposite());
    IFluidHandler toFill = getFluidHandler(pos.down(), EnumFacing.UP);
    if(toDrain != null && toFill != null) {
      // can we drain?
      FluidStack drained = toDrain.drain(TRANSACTION_AMOUNT, false);
      if(drained != null && (Config.drainGaseousFluids || !drained.getFluid().isGaseous())) {
        // can we fill?
        int filled = toFill.fill(drained, false);
        if(filled > 0) {
          // drain the liquid and transfer it, buffer the amount for delay
          this.drained = toDrain.drain(filled, true);
          this.isPouring = true;
          pour();

          // sync to clients
          if(getWorld() instanceof WorldServer && !getWorld().isRemote) {
            TinkerNetwork.sendToClients((WorldServer) getWorld(), pos, new FaucetActivationPacket(pos, drained));
          }

          return;
        }
      }
    }
    // draining unsuccessful
    reset();
  }

  // takes the liquid inside and executes one pouring step
  protected void pour() {
    if(drained == null) {
      return;
    }

    IFluidHandler toFill = getFluidHandler(pos.down(), EnumFacing.UP);
    if(toFill != null) {
      FluidStack fillStack = drained.copy();
      fillStack.amount = Math.min(drained.amount, Config.liquidTransferRate);

      // can we fill?
      int filled = toFill.fill(fillStack, false);
      if(filled > 0) {
        // transfer it
        this.drained.amount -= filled;
        fillStack.amount = filled;
        toFill.fill(fillStack, true);
      }
    }
    else {
      // filling TE got lost. reset. all liquid buffered is lost.
      reset();
    }
  }

  protected void reset() {
    isPouring = false;
    stopPouring = false;
    drained = null;
    direction = EnumFacing.DOWN; // invalid direction
    lastRedstoneState = false;

    // sync to clients
    if(getWorld() instanceof WorldServer && !getWorld().isRemote) {
      TinkerNetwork.sendToClients((WorldServer) getWorld(), pos, new FaucetActivationPacket(pos, null));
    }
  }

  protected IFluidHandler getFluidHandler(BlockPos pos, EnumFacing direction) {
    TileEntity te = getWorld().getTileEntity(pos);
    if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction)) {
      return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction);
    }
    return null;
  }

  // faucet flow may be in the block below, so still render if looking at that
  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
  }

  /* Load & Save */

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    if(drained != null) {
      drained.writeToNBT(compound);
      compound.setInteger("direction", direction.getIndex());
      //compound.setString("direction", direction.getName());
      compound.setBoolean("stop", stopPouring);
    }
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    drained = FluidStack.loadFluidStackFromNBT(compound);

    if(drained != null) {
      isPouring = true;
      direction = EnumFacing.values()[compound.getInteger("direction")];
      //direction = EnumFacing.valueOf(compound.getString("direction"));
      stopPouring = compound.getBoolean("stop");
    }
    else {
      reset();
    }
  }

  public void onActivationPacket(FluidStack fluid) {
    if(fluid == null) {
      reset();
    }
    else {
      drained = fluid;
      isPouring = true;
      direction = getWorld().getBlockState(pos).getValue(BlockFaucet.FACING);
    }
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    readFromNBT(pkt.getNbtCompound());
  }

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
    boolean wasPouring = isPouring;
    readFromNBT(tag);

    // Check if the sound should be played on load
    if (!wasPouring && isPouring) {
      TinkerSmeltery.proxy.playSound(getSound());
    }
  }

  @Override
  public SoundType getSoundType() {
    return SoundType.FAUCET;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ISound getSound() {
    return new SoundFading(this, pos);
  }

  @Override
  public boolean shouldPlaySound() {
    return !this.isInvalid() && this.isPouring;
  }
}
