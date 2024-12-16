package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;

public interface ICommand extends Comparable<ICommand>
{
    /**
     * Gets the name of the command
     */
    String getCommandName();

    /**
     * Gets the usage string for the command.
     *  
     * @param sender The command sender that executed the command
     */
    String getCommandUsage(ICommandSender sender);

    List<String> getCommandAliases();

    /**
     * Callback when the command is invoked
     *  
     * @param sender The command sender that executed the command
     * @param args The arguments that were passed
     */
    void processCommand(ICommandSender sender, String[] args) throws CommandException;

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    boolean canCommandSenderUseCommand(ICommandSender sender);

    List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos);

    /**
     * Return whether the specified command parameter index is a username parameter.
     *  
     * @param args The arguments that were passed
     */
    boolean isUsernameIndex(String[] args, int index);
}