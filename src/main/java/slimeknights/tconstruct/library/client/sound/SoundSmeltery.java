package slimeknights.tconstruct.library.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructure;

@SideOnly(Side.CLIENT)
public class SoundSmeltery extends PositionedSound implements ITickableSound {
    protected TileHeatingStructure<?> heatingStructure;
    protected BlockPos position;

    public SoundSmeltery(TileHeatingStructure<?> heatingStructure, float volume) {
        super(Sounds.smeltery_loop, SoundCategory.BLOCKS);
        this.repeat = true;
        this.heatingStructure = heatingStructure;
        this.volume = volume;
        this.position = this.heatingStructure.getPos();
        this.xPosF = position.getX();
        this.yPosF = position.getY();
        this.zPosF = position.getZ();
    }

    @Override
    public void update() {
        if(this.heatingStructure.isInvalid() || !this.heatingStructure.isActive() || !this.heatingStructure.isHeating) {
            this.volume -= 0.02F;
        }
    }

    @Override
    public boolean isDonePlaying() {
        return this.volume <= 0.0F;
    }
}
