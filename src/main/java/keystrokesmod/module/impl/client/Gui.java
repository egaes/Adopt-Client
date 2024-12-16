package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

public class Gui extends Module {
    public static SliderSetting guiScale;
    public static SliderSetting backgroundBlur;
    public static SliderSetting scrollSpeed;
    public static ButtonSetting rainBowOutlines;

    public Gui() {
        super("Gui", category.client, 54);
        this.registerSetting(guiScale = new SliderSetting("Gui scale", 1, new String[]{ "Small", "Normal", "Large" }));
        this.registerSetting(backgroundBlur = new SliderSetting("Background blur", "%", 50, 0, 100, 1));
        this.registerSetting(scrollSpeed = new SliderSetting("Scroll speed", 50, 0, 90, 1));
        this.registerSetting(rainBowOutlines = new ButtonSetting("Rainbow outlines", true));
    }

    public void onEnable() {
        if (Utils.nullCheck() && mc.currentScreen != Raven.clickGui) {
            mc.displayGuiScreen(Raven.clickGui);
            Raven.clickGui.initMain();
        }

        this.disable();
    }
}
