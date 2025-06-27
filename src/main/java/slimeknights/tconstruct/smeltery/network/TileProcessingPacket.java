package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class TileProcessingPacket extends AbstractPacketThreadsafe {
    public BlockPos pos;
    public boolean isProcessing;

    public TileProcessingPacket() {
    }

    public TileProcessingPacket(BlockPos pos, boolean isProcessing) {
        this.pos = pos;
        this.isProcessing = isProcessing;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
        if (tile instanceof ITileInfoPacketHandler) {
            ((ITileInfoPacketHandler) tile).onTileInfoPacket(this);
        }
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        throw new UnsupportedOperationException("Clientside only");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = new BlockPos(BlockPos.fromLong(buf.readLong()));
        isProcessing = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeBoolean(isProcessing);
    }
}
