package dev.quarris.engulfingdarkness;

import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModRef {

    public static final String ID = "engulfingdarkness";

    public static final Logger LOGGER = LogManager.getLogger(EngulfingDarkness.class);

    public static ResourceLocation res(String res) {
        return new ResourceLocation(ID, res);
    }

}
