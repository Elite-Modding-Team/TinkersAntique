package slimeknights.tconstruct.library.sound;

import net.minecraft.client.audio.ISound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISoundSource {
    SoundType getSoundType();

    @SideOnly(Side.CLIENT)
    ISound getSound();

    boolean shouldPlaySound();
}
