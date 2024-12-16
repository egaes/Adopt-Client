package net.minecraft.command;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerCommand
{
    /** The command string. */
    public final String command;
    public final ICommandSender sender;

    public ServerCommand(String input, ICommandSender sender)
    {
        this.command = input;
        this.sender = sender;
    }
}