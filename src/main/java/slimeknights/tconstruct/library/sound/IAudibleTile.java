package slimeknights.tconstruct.library.sound;

import net.minecraft.world.World;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.network.UpdateSoundPacket;

public interface IAudibleTile {
    default void sendSoundPacket(World world) {
        Sounds.playSoundForAll(world, getSoundPacket());
    }

    UpdateSoundPacket getSoundPacket();

    void onSoundPacket(UpdateSoundPacket packet);
}
