package keystrokesmod.mixins.impl.entity;

import keystrokesmod.event.PrePlayerInputEvent;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.SafeWalk;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static keystrokesmod.Raven.mc;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    public double motionX;

    @Shadow public double motionZ;

    @ModifyVariable(method = "moveEntity", at = @At(value = "STORE", ordinal = 0), name = "flag")
    private boolean injectSafeWalk(boolean flag) {
        Entity entity = (Entity) (Object) this;
        Minecraft mc = Minecraft.getMinecraft();

        if (entity == mc.thePlayer && entity.onGround) {
            if (SafeWalk.canSafeWalk() || ModuleManager.scaffold.safewalk()) {
                return true;
            }
        }
        return flag;
    }

    /**
     * @author strangerrs
     * @reason moveFlying mixin
     */
    @Inject(method = "moveFlying", at = @At("HEAD"), cancellable = true)
    public void moveFlying(float p_moveFlying_1_, float p_moveFlying_2_, float p_moveFlying_3_, CallbackInfo ci) {
        float yaw = ((Entity)(Object) this).rotationYaw;
        if((Object) this instanceof EntityPlayerSP) {
            PrePlayerInputEvent prePlayerInput = new PrePlayerInputEvent(p_moveFlying_1_, p_moveFlying_2_, p_moveFlying_3_, mc.thePlayer.rotationYaw);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(prePlayerInput);
            if (prePlayerInput.isCanceled()) {
                ci.cancel();
                return;
            }
            p_moveFlying_1_ = prePlayerInput.getStrafe();
            p_moveFlying_2_ = prePlayerInput.getForward();
            p_moveFlying_3_ = prePlayerInput.getFriction();
            yaw = prePlayerInput.getYaw();
        }

        float f = p_moveFlying_1_ * p_moveFlying_1_ + p_moveFlying_2_ * p_moveFlying_2_;
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0F) {
                f = 1.0F;
            }

            f = p_moveFlying_3_ / f;
            p_moveFlying_1_ *= f;
            p_moveFlying_2_ *= f;
            float f1 = MathHelper.sin(yaw * 3.1415927F / 180.0F);
            float f2 = MathHelper.cos(yaw * 3.1415927F / 180.0F);
            this.motionX += p_moveFlying_1_ * f2 - p_moveFlying_2_ * f1;
            this.motionZ += p_moveFlying_2_ * f2 + p_moveFlying_1_ * f1;
        }
        ci.cancel();
    }
}