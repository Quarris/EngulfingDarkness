package dev.quarris.engulfingdarkness.darkness;

import net.minecraft.world.item.ItemStack;

public interface IDarkness {

    float MAX_BURNOUT = 32.0f;

    /**
     * This value increases as the player is in darkness. This is used for the fog that encloses on the player.
     * Once the value reaches 1, the player is considered engulfed.
     * @return How much the darkness has engulfed the player. Value is between 0 and 1.
     */
    float getDarknessLevel();

    /**
     * Once engulfed, the danger levels increase. Once reached 1, the player will take damage.
     * @return The danger level, value between 0 and 1.
     */
    float getDangerLevel();

    /**
     * Gets the current life of the held light.
     * @return The life of the currently held light, value is between 0 and 1.
     */
    float getFlameLife();

    /**
     * @return true is the player is currently holding a LightBringer item.
     */
    boolean isHoldingFlame();

    boolean isResistant();

    void tick();

    void setInDarkness(boolean inDarkness);

    boolean isInDarkness();

    float getBurnoutModifier();

    LightBringer getLight(ItemStack stack);
}
