package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;

public class Chams extends Module {
    private ButtonSetting ignoreBots;
    private ButtonSetting renderSelf;
    private HashSet<Entity> bots = new HashSet<>();

    public Chams() {
        super("Chams", Module.category.render, 0);
        this.registerSetting(ignoreBots = new ButtonSetting("Ignore bots", false));
        this.registerSetting(renderSelf = new ButtonSetting("Render self", false));
    }

    @SubscribeEvent
    public void r1(Pre e) {
        if (e.entity == mc.thePlayer && !renderSelf.isToggled()) {
            return;
        }
        if (ignoreBots.isToggled()) {
            if (AntiBot.isBot(e.entity)) {
                return;
            }
            this.bots.add(e.entity);
        }
        GL11.glEnable(32823);
        GL11.glPolygonOffset(1.0f, -2500000.0f);
    }

    @SubscribeEvent
    public void r2(Post e) {
        if (e.entity == mc.thePlayer && !renderSelf.isToggled()) {
            return;
        }
        if (ignoreBots.isToggled()) {
            if (!this.bots.contains(e.entity)) {
                return;
            }
            this.bots.remove(e.entity);
        }
        GL11.glDisable(32823);
        GL11.glPolygonOffset(1.0f, 2500000.0f);
    }

    public void onDisable() {
        this.bots.clear();
    }
}
