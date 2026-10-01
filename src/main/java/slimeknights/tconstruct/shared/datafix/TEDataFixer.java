package slimeknights.tconstruct.shared.datafix;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;
import slimeknights.tconstruct.library.Util;

public class TEDataFixer implements IFixableData {
    private final String oldPrefix = "minecraft:tconstruct.";
    private final String newPrefix = Util.MODID + ":";

    @Override
    public int getFixVersion() {
        return 1;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
        String id = compound.getString("id");
        if (id.startsWith(oldPrefix)) {
            compound.setString("id", id.replace(oldPrefix, newPrefix));
        }
        return compound;
    }
}
