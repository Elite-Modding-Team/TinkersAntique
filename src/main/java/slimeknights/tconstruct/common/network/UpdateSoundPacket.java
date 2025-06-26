package slimeknights.tconstruct.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tconstruct.library.client.sound.SoundType;
import slimeknights.tconstruct.library.sound.IAudibleTile;

public class UpdateSoundPacket extends AbstractPacketThreadsafe {
    public SoundType soundType;
    public BlockPos pos;
    public float volume;
    public int[] data;

    public UpdateSoundPacket() {
    }

    public UpdateSoundPacket(SoundType soundType, BlockPos pos, float volume, int... data) {
        this.soundType = soundType;
        this.pos = pos;
        this.volume = volume;
        this.data = data;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        TileEntity tile = Minecraft.getMinecraft().world.getTileEntity(pos);
        if (tile instanceof IAudibleTile) {
            ((IAudibleTile) tile).onSoundPacket(this);
        }
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        // clients have sounds, servers don't!
        throw new UnsupportedOperationException("Clientside only");
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        soundType = SoundType.values()[buf.readInt()];
        pos = new BlockPos(BlockPos.fromLong(buf.readLong()));
        volume = buf.readFloat();

        data = new int[buf.readInt()];
        for(int i = 0; i < data.length; i++) {
            data[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(soundType.ordinal());
        buf.writeLong(pos.toLong());
        buf.writeFloat(volume);

        buf.writeInt(data.length);
        for(int i : data) {
            buf.writeInt(i);
        }
    }
}
