package xyz.vxndicate.rosebloomtpa;

import xyz.vxndicate.rosebloomtpa.manager.TeleportManager;
import xyz.vxndicate.rosebloomtpa.manager.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPACommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            sender.sendMessage(main.getPrefix() + "§4You need to be a player.");
            return false;
        }

        final Player player = ((Player) sender);

        if(args.length == 1){

            final Player target = Bukkit.getPlayer(args[0]);

            if(target != null){

                TeleportManager.sendRequest(player, target, TeleportType.NORMAL);

            }else{
                player.sendMessage(main.getPrefix() + "§4This player is not online!");
            }

        }else{
            player.sendMessage(main.getPrefix() + "§4Usage: /tpa <Player>");
        }



        return false;
    }
}
