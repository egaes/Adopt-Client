package keystrokesmod.script.packets.serverbound;

import keystrokesmod.script.packets.clientbound.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S3EPacketTeams;

public class PacketHandler {
    public static CPacket convertServerBound(net.minecraft.network.Packet packet) {
        if (packet == null || packet.getClass().getSimpleName().startsWith("S")) {
            return null;
        }
        CPacket newPacket;
        try {
            if (packet instanceof C0APacketAnimation) {
                newPacket = new C0A((C0APacketAnimation) packet);
            }
            else if (packet instanceof C0BPacketEntityAction) {
                newPacket = new C0B((C0BPacketEntityAction) packet);
            }
            else if (packet instanceof C01PacketChatMessage) {
                newPacket = new C01((C01PacketChatMessage)packet);
            }
            else if (packet instanceof C02PacketUseEntity) {
                newPacket = new C02((C02PacketUseEntity)packet);
            }
            else if (packet instanceof C0FPacketConfirmTransaction) {
                newPacket = new C0F((C0FPacketConfirmTransaction) packet);
            }
            else if (packet instanceof C0EPacketClickWindow) {
                newPacket = new C0E((C0EPacketClickWindow) packet);
            }
            else if (packet instanceof C03PacketPlayer) {
                newPacket = new C03((C03PacketPlayer)packet, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
            }
            else if (packet instanceof C07PacketPlayerDigging) {
                newPacket = new C07((C07PacketPlayerDigging)packet);
            }
            else if (packet instanceof C08PacketPlayerBlockPlacement) {
                newPacket = new C08((C08PacketPlayerBlockPlacement)packet);
            }
            else if (packet instanceof C09PacketHeldItemChange) {
                newPacket = new C09(((C09PacketHeldItemChange)packet), true);
            }
            else if (packet instanceof C10PacketCreativeInventoryAction) {
                newPacket = new C10((C10PacketCreativeInventoryAction) packet);
            }
            else if (packet instanceof C13PacketPlayerAbilities) {
                newPacket = new C13((C13PacketPlayerAbilities) packet);
            }
            else if (packet instanceof C16PacketClientStatus) {
                newPacket = new C16((C16PacketClientStatus) packet);
            }
            else if (packet instanceof C0DPacketCloseWindow) {
                newPacket = new C0D((C0DPacketCloseWindow) packet);
            }
            else {
                newPacket = new CPacket(packet);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            newPacket = null;
        }
        return newPacket;
    }

    public static SPacket convertClientBound(Packet packet) {
        SPacket sPacket;
        try {
            if (packet instanceof S12PacketEntityVelocity) {
                sPacket = new S12((S12PacketEntityVelocity)packet);
            }
            else if (packet instanceof S27PacketExplosion) {
                sPacket = new S27((S27PacketExplosion)packet);
            }
            else if (packet instanceof S3EPacketTeams) {
                sPacket = new S3E((S3EPacketTeams) packet);
            }
            else if (packet instanceof S08PacketPlayerPosLook) {
                sPacket = new S08((S08PacketPlayerPosLook) packet);
            }
            else {
                sPacket = new SPacket(packet);
            }
        }
        catch (Exception ex) {
            sPacket = null;
        }
        return sPacket;
    }

    public static Packet convertCPacket(CPacket cPacket) {
        try {
            if (cPacket instanceof C0A) {
                return new C0APacketAnimation();
            }
            else if (cPacket instanceof C0B) {
                return ((C0B) cPacket).convert();
            }
            else if (cPacket instanceof C0D) {
                return ((C0D) cPacket).convert();
            }
            else if (cPacket instanceof C09) {
                return ((C09) cPacket).convert();
            }
            else if (cPacket instanceof C0E) {
                return ((C0E) cPacket).convert();
            }
            else if (cPacket instanceof C0F) {
                return ((C0F) cPacket).convert();
            }
            else if (cPacket instanceof C08) {
                return ((C08) cPacket).convert();
            }
            else if (cPacket instanceof C07) {
                return ((C07) cPacket).convert();
            }
            else if (cPacket instanceof C01) {
                return ((C01) cPacket).convert();
            }
            else if (cPacket instanceof C02) {
                return ((C02) cPacket).convert();
            }
            else if (cPacket instanceof C03) {
                return cPacket.packet;
            }
            else if (cPacket instanceof C10) {
                return ((C10) cPacket).convert();
            }
            else if (cPacket instanceof C13) {
                return ((C13) cPacket).convert();
            }
            else if (cPacket instanceof C16) {
                return ((C16) cPacket).convert();
            }
        }
        catch (Exception e) {
            if (cPacket != null && cPacket.packet != null && !cPacket.name.startsWith("S")) {
                return cPacket.packet;
            }
            else {
                return null;
            }
        }
        if (cPacket == null && cPacket.packet == null) {
            return null;
        }
        return cPacket.packet;
    }
}
