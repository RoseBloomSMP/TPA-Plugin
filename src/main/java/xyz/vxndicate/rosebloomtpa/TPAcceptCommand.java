package xyz.vxndicate.rosebloomtpa;

import xyz.vxndicate.rosebloomtpa.manager.TeleportManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAcceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage(main.getPrefix() + "§4You need to be a player.");
            return false;
        }

        final Player player = ((Player) sender);

        if(args.length == 0){

            TeleportManager.acceptLastRequest(player);

        }else if(args.length == 1){

            TeleportManager.acceptRequest(player, args[0]);

        }else{
            player.sendMessage(main.getPrefix() + "§4Usage: /tpaccept [Player]");
        }


        return false;
    }
}
