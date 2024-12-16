package net.minecraft.client.gui.spectator;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISpectatorMenuObject
{
    void func_178661_a(SpectatorMenu menu);

    IChatComponent getSpectatorName();

    void func_178663_a(float p_178663_1_, int alpha);

    boolean func_178662_A_();
}