package dev.quarris.engulfingdarkness.capability;

public interface IDarkness {

    float MAX_BURNOUT = 32.0f;

    float getDarkness();

    float getBurnout();

    void setInDarkness(boolean inDarkness);

    boolean isResistant();

    void tick();

    boolean isInDarkness();

    float getDanger();

    void resetBurnout();

    void syncToClient();
}
