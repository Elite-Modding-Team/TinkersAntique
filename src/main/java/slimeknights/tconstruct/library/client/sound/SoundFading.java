package slimeknights.tconstruct.library.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.sound.ISoundSource;

@SideOnly(Side.CLIENT)
public class SoundFading extends PositionedSound implements ITickableSound {
    protected ISoundSource source;

    public SoundFading(ISoundSource source, BlockPos pos) {
        super(source.getSoundType().soundEvent(), SoundCategory.BLOCKS);
        this.repeat = true;
        this.source = source;
        this.volume = source.getSoundType().volume();
        this.xPosF = pos.getX() + 0.5F;
        this.yPosF = pos.getY() + 0.5F;
        this.zPosF = pos.getZ() + 0.5F;
    }

    @Override
    public void update() {
        if(!source.shouldPlaySound()) {
            this.volume -= 0.02F;
        }
    }

    @Override
    public boolean isDonePlaying() {
        return this.volume <= 0.0F;
    }
}
