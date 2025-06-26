package slimeknights.tconstruct.library.sound;

import net.minecraft.util.SoundEvent;

import slimeknights.tconstruct.common.Sounds;

public enum SoundType {
    // Smeltery
    FAUCET(1.0F, Sounds.faucet_pour_loop),
    HEATING_STRUCTURE(0.8F, Sounds.smeltery_loop);

    final float volume;
    final SoundEvent soundEvent;

    SoundType(float volume, SoundEvent soundEvent) {
        this.volume = volume;
        this.soundEvent = soundEvent;
    }

    public float volume() {
        return volume;
    }

    public SoundEvent soundEvent() {
        return soundEvent;
    }
}
