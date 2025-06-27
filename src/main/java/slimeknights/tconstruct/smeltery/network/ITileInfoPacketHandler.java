package slimeknights.tconstruct.smeltery.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import slimeknights.tconstruct.common.TinkerNetwork;

public interface ITileInfoPacketHandler {
    default void sendTileInfo(World world, BlockPos pos) {
        if (world.isRemote) return;
        TinkerNetwork.sendToAllTracking(getTileInfoPacket(), new NetworkRegistry.TargetPoint(
                world.provider.getDimension(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                0.0D    // Ignored
        ));
    }

    TileProcessingPacket getTileInfoPacket();

    void onTileInfoPacket(TileProcessingPacket packet);
}
