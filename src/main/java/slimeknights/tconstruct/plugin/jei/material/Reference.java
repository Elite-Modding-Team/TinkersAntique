package slimeknights.tconstruct.plugin.jei.material;

import slimeknights.tconstruct.library.materials.MaterialTypes;

import java.util.ArrayList;
import java.util.List;

public final class Reference {
    public static final List<String> HARVEST_TYPES = new ArrayList<>();
    public static final List<String> RANGED_TYPES = new ArrayList<>();
    public static final List<String> PROJECTILE_TYPES = new ArrayList<>();

    static {
        HARVEST_TYPES.add(MaterialTypes.HEAD);
        HARVEST_TYPES.add(MaterialTypes.EXTRA);
        HARVEST_TYPES.add(MaterialTypes.HANDLE);

        RANGED_TYPES.add(MaterialTypes.BOW);
        RANGED_TYPES.add(MaterialTypes.BOWSTRING);

        PROJECTILE_TYPES.add(MaterialTypes.PROJECTILE);
        PROJECTILE_TYPES.add(MaterialTypes.SHAFT);
        PROJECTILE_TYPES.add(MaterialTypes.FLETCHING);
    }
}
