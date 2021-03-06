package draylar.identity.cca;

import nerdhub.cardinal.components.api.component.Component;
import net.minecraft.nbt.CompoundTag;

public class HostilityComponent implements Component {

    private int remainingTime = 0;

    public HostilityComponent() {

    }

    @Override
    public void fromTag(CompoundTag tag) {
        tag.putInt("RemainingTime", remainingTime);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.remainingTime = tag.getInt("RemainingTime");
        return tag;
    }

    /**
     * Sets this components' hostility timer to the given time in ticks.
     *
     * @param time  time, in ticks, to set hostility timer to
     */
    public void setHostility(int time) {
        this.remainingTime = time;
    }

    /**
     * Returns whether the player this component is attached to will be targeted by hostile mobs, regardless of Identity.
     *
     * <p>Hostility works on a timer, and is set when the player attacks a hostile mob.
     *
     * @return  whether this component's player will be targeted by hostile mobs, regardless of Identity
     */
    public boolean hasHostility() {
        return remainingTime > 0;
    }

    /**
     * Ticks this hostility component. 0 means no hostility, so 15 * 20 means it will expire after 15 seconds.
     */
    public void tickHostility() {
        remainingTime = Math.max(0, remainingTime - 1);
    }
}
