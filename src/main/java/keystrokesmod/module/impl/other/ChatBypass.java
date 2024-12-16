package keystrokesmod.module.impl.other;

import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

public class ChatBypass extends Module {
    private ButtonSetting filterKnownWords;
    private List<String> filteredWords = Arrays.asList("nigga", "retard", "nigger", "faggot", "chink", "tranny", "trans", "nigglet", "niggers", "tran", "trannies", "trannie");

    public ChatBypass() {
        super("Chat Bypass", category.other);
        this.registerSetting(filterKnownWords = new ButtonSetting("Only filter known words", true));
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (e.getPacket() instanceof C01PacketChatMessage) {
            try {
                String message = applyBypass((String) Reflection.C01PacketChatMessageMessage.get(e.getPacket()));
                Reflection.C01PacketChatMessageMessage.set(e.getPacket(), message);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String applyBypass(String message) {
        if (filterKnownWords.isToggled()) {
            for (String word : filteredWords) {
                if (message.toLowerCase().contains(word)) {
                    break;
                }
                else {
                    return message;
                }
            }
        }

        if (message.contains("i")) {
            return message.replace("i", "¡");
        }
        if (message.contains("a")) {
            return message.replace("a", "á");
        }

        return message;
    }
}