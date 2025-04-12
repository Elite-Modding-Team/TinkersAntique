package slimeknights.tconstruct.library.client.sound;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;

@SideOnly(Side.CLIENT)
public class SoundFaucet extends PositionedSound implements ITickableSound {
    protected TileFaucet faucet;
    protected BlockPos position;

    public SoundFaucet(TileFaucet faucet, float volume) {
        super(Sounds.faucet_pour_loop, SoundCategory.BLOCKS);
        this.repeat = true;
        this.faucet = faucet;
        this.volume = volume;
        this.position = this.faucet.getPos();
        this.xPosF = position.getX();
        this.yPosF = position.getY();
        this.zPosF = position.getZ();
    }

    @Override
    public void update() {
        if(this.faucet.isInvalid() || !this.faucet.isPouring) {
            this.volume -= 0.05F;
        }
    }

    @Override
    public boolean isDonePlaying() {
        return this.volume <= 0.0F;
    }
}
