package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
    private boolean received;
    private float[] rotations;
    public NoRotate() {
        super("NoRotate", category.player);
    }

    @Override
    public void onDisable() {
        received = false;
        rotations = null;
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent event) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            try {
                Reflection.S08PacketPlayerPosLookYaw.set(packet, mc.thePlayer.rotationYaw);
                Reflection.S08PacketPlayerPosLookPitch.set(packet, mc.thePlayer.rotationPitch);
            }
            catch (Exception e) {
                e.printStackTrace();
                Utils.sendModuleMessage(this, "&cFailed to modify S08PacketPlayerPosLook. Relaunch your game.");
                return;
            }
            received = true;
            rotations = new float[]{packet.getYaw(), packet.getPitch()};
        }
    }

    @SubscribeEvent
    public void onPacket(SendPacketEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (received && rotations != null && (e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook || e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook)) {
            C03PacketPlayer.C06PacketPlayerPosLook packet = (C03PacketPlayer.C06PacketPlayerPosLook) e.getPacket();
            try {
                Reflection.C03PacketPlayerYaw.set(packet, rotations[0]);
                Reflection.C03PacketPlayerPitch.set(packet, rotations[1]);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                Utils.sendModuleMessage(this, "&cFailed to modify C06PacketPlayerPosLook. Relaunch your game.");
            }
            received = false;
            rotations = null;
        }
    }
}
