package net.minecraft.entity.boss;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBossDisplayData
{
    float getMaxHealth();

    float getHealth();

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    IChatComponent getDisplayName();
}