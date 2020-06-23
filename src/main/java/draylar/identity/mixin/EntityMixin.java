package draylar.identity.mixin;

import draylar.identity.impl.DimensionsRefresher;
import draylar.identity.registry.Components;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements DimensionsRefresher {

    @Shadow private EntityDimensions dimensions;
    @Shadow public abstract EntityPose getPose();
    @Shadow public abstract EntityDimensions getDimensions(EntityPose pose);
    @Shadow public abstract Box getBoundingBox();
    @Shadow public abstract void setBoundingBox(Box boundingBox);
    @Shadow protected boolean firstUpdate;
    @Shadow public World world;
    @Shadow public abstract void move(MovementType type, Vec3d movement);
    @Shadow private float standingEyeHeight;
    @Shadow protected abstract float getEyeHeight(EntityPose pose, EntityDimensions dimensions);

    @Inject(
            method = "getWidth",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getWidth(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;
            LivingEntity Identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

            if (Identity != null) {
                cir.setReturnValue(Identity.getWidth());
            }
        }
    }

    @Inject(
            method = "getHeight",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getHeight(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;
            LivingEntity Identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

            if (Identity != null) {
                cir.setReturnValue(Identity.getHeight());
            }
        }
    }

    @Override
    public void refresh() {
        EntityDimensions currentDimensions = this.dimensions;
        EntityPose entityPose = this.getPose();
        EntityDimensions newDimensions = this.getDimensions(entityPose);

        this.dimensions = newDimensions;
        this.standingEyeHeight = this.getEyeHeight(entityPose, newDimensions);

        Box box = this.getBoundingBox();
        this.setBoundingBox(new Box(box.x1, box.y1, box.z1, box.x1 + (double) newDimensions.width, box.y1 + (double) newDimensions.height, box.z1 + (double) newDimensions.width));

        if (!this.firstUpdate && !this.world.isClient) {
            float f = currentDimensions.width - newDimensions.width;
            this.move(MovementType.SELF, new Vec3d(f, 0.0D, f));
        }
    }

    @Inject(
            method = "isFireImmune",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isFireImmune(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) (Object) this;
            LivingEntity Identity = Components.CURRENT_IDENTITY.get(playerEntity).getIdentity();

            if (Identity != null) {
                cir.setReturnValue(Identity.getType().isFireImmune());
            }
        }
    }
}