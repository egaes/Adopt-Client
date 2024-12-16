package keystrokesmod.mixins.impl.client;

import keystrokesmod.module.ModuleManager;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldInfo.class)
public abstract class MixinWorldInfo {

    @Shadow
    private long worldTime;

    @Inject(method = "getWorldTime", at = @At("RETURN"), cancellable = true)
    public void injectGetWorldTime(CallbackInfoReturnable<Long> cir) {
        if (ModuleManager.weather != null && ModuleManager.weather.isEnabled()) {
            cir.setReturnValue((long) (ModuleManager.weather.time.getInput() * 1000));
        }
    }

    @Inject(method = "isRaining", at = @At("RETURN"), cancellable = true)
    private void setPrecipitation(CallbackInfoReturnable<Boolean> clr) {
        if (ModuleManager.weather != null && ModuleManager.weather.isEnabled() && ModuleManager.weather.rain.isToggled()) {
            clr.setReturnValue(true);
        }
    }
}