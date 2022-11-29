package xyz.vxndicate.rosebloomtpa.manager;

import xyz.vxndicate.rosebloomtpa.main;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeleportManager implements Listener {

    @Getter
    private static HashMap<UUID, NavigableMap<String, TeleportUser>> newRequests = new HashMap<>();

    @Getter
    private static HashMap<UUID, Integer> delay = new HashMap<>();

    public static void sendRequest(Player sender, Player becommer, TeleportType teleportType){
        if(sender == becommer){
            sender.sendMessage(main.getPrefix() + "§4You can't send yourself an tpa request!");
            return;
        }

        if(newRequests.containsKey(becommer.getUniqueId())){
            NavigableMap<String, TeleportUser> list = newRequests.get(becommer.getUniqueId());
            if(list.containsKey(sender.getName())){
                TeleportUser teleportUser = list.get(sender.getName());
                if(teleportType == teleportUser.getTeleportType()){
                    sender.sendMessage(main.getPrefix() + "§4You've already send an tpa request to this player!");
                    return;
                }
            }
        }
        newRequests.put(becommer.getUniqueId(), addToList(sender.getName(), new TeleportUser(sender.getName(), teleportType, sender.getLocation()), newRequests.get(becommer.getUniqueId())));
        sender.sendMessage(main.getPrefix() + "§cYour teleport request has been sent!");
        if(teleportType == TeleportType.NORMAL){
            becommer.sendMessage(main.getPrefix() + "§6" + sender.getName() + " §cwants to teleport to you.");
            becommer.spigot().sendMessage(getAcceptMessage(sender));
            return;
        }
        becommer.sendMessage(main.getPrefix() + "§6" + sender.getName() + " §cwants you to teleport to him.");
        becommer.spigot().sendMessage(getAcceptMessage(sender));
    }

    public static void sendRequestToAll(Player sender){
        Bukkit.getOnlinePlayers().forEach(all -> {
            if(sender == all){
                return;
            }
            newRequests.put(all.getUniqueId(), addToList(sender.getName(), new TeleportUser(sender.getName(), TeleportType.AllHERE, sender.getLocation()), newRequests.get(all.getUniqueId())));
            all.sendMessage(main.getPrefix() + "§6" + sender.getName() + " §cwants you to teleport to him.");
            all.spigot().sendMessage(getAcceptMessage(sender));
        });
        sender.sendMessage(main.getPrefix() + "§cYou have sent EVERYONE a teleport request.");
    }

    public static void acceptRequest(Player becommer, String acceptName){
        if(newRequests.containsKey(becommer.getUniqueId())) {
            NavigableMap<String, TeleportUser> list = newRequests.get(becommer.getUniqueId());
            if (list.containsKey(acceptName)) {
                TeleportUser teleportUser = list.get(acceptName);
                Player sender = Bukkit.getPlayer(teleportUser.getName());
                if (sender != null) {
                    if(teleportUser.getTeleportType() == TeleportType.AllHERE){
                        becommer.teleport(teleportUser.getHereLoc());
                        becommer.sendMessage(main.getPrefix() + "Teleporting...");
                        sender.sendMessage(main.getPrefix() + "§6" + becommer.getName() + " §caccepted your teleport request.");
                    }else{
                        startDelay(sender, becommer, teleportUser.getTeleportType(), teleportUser.getHereLoc());
                    }
                    list.remove(acceptName);
                    newRequests.put(becommer.getUniqueId(), list);
                } else {
                    list.remove(acceptName);
                    becommer.sendMessage(main.getPrefix() + "§4This player is not online!");
                    newRequests.put(becommer.getUniqueId(), list);
                }
            } else {
                becommer.sendMessage(main.getPrefix() + "§4You have no open teleport requests with §6" + acceptName + "§4.");
            }
        }else{
            becommer.sendMessage(main.getPrefix() + "§4You have no open teleport requests from §6" + acceptName + "§4.");
        }
    }

    public static void acceptLastRequest(Player becommer){
        if(newRequests.containsKey(becommer.getUniqueId())) {
            NavigableMap<String, TeleportUser> list = newRequests.get(becommer.getUniqueId());
            if (!list.isEmpty()) {
                Map.Entry<String, TeleportUser> entry = list.firstEntry();
                Player sender = Bukkit.getPlayer(entry.getValue().getName());
                if (sender != null) {
                    if(entry.getValue().getTeleportType() == TeleportType.AllHERE){
                        becommer.teleport(entry.getValue().getHereLoc());
                        becommer.sendMessage(main.getPrefix() + "Teleporting...");
                        sender.sendMessage(main.getPrefix() + "§6" + becommer.getName() + " §caccepted your teleport request.");
                    }else{
                        startDelay(sender, becommer, entry.getValue().getTeleportType(), entry.getValue().getHereLoc());
                    }
                    list.clear();
                    newRequests.put(becommer.getUniqueId(), list);
                } else {
                    list.clear();
                    becommer.sendMessage(main.getPrefix() + "§4This player isn't online.");
                    newRequests.put(becommer.getUniqueId(), list);
                }
            } else {
                becommer.sendMessage(main.getPrefix() + "§4You have no open teleport requests.");
            }
        }else {
            becommer.sendMessage(main.getPrefix() + "§4You have no open teleport requests.");
        }
    }

    private static NavigableMap<String, TeleportUser> addToList(String name, TeleportUser teleportUser, NavigableMap<String, TeleportUser> list){
        if(list != null){
            list.put(name, teleportUser);
        }else{
            list = new TreeMap<>();
            list.put(name, teleportUser);
        }
        return list;
    }

    private static void startDelay(final Player sender, final Player becommer, final TeleportType teleportType, final Location hereLoc){

        if(teleportType == TeleportType.NORMAL){
            delay.put(sender.getUniqueId(), 60);
            sender.sendMessage(main.getPrefix() + "§cYou accepted §6" + becommer.getName() + "'s §cteleport request.");
            sender.sendMessage(main.getPrefix() + "§4Do not move. Starting teleportation...");
            becommer.sendMessage(main.getPrefix() + "§6" + becommer.getName() + " accepted your teleport request.");
        }else{
            delay.put(becommer.getUniqueId(), 60);
            becommer.sendMessage(main.getPrefix() + "§cYou accepted §6" + sender.getName() + "'s §cteleport request.");
            becommer.sendMessage(main.getPrefix() + "§4Do not move. Starting teleportation...");
            sender.sendMessage(main.getPrefix() + "§6" + becommer.getName() + " accepted your teleport request.");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(teleportType == TeleportType.NORMAL){
                    if(delay.containsKey(sender.getUniqueId())){

                        if(delay.get(sender.getUniqueId()) == 0){
                            delay.remove(sender.getUniqueId());
                            sender.teleport(becommer);
                            sender.sendMessage(main.getPrefix() + "§cTeleporting...");
                            cancel();
                            return;
                        }
                        delay.put(sender.getUniqueId(), delay.get(sender.getUniqueId())-1);
                    }else{
                        sender.sendMessage(main.getPrefix() + "§4Teleport cancelled.");
                        cancel();
                    }
                }else{
                    if(delay.containsKey(becommer.getUniqueId())){
                        if(delay.get(becommer.getUniqueId()) == 0){
                            delay.remove(becommer.getUniqueId());
                            becommer.teleport(hereLoc);
                            becommer.sendMessage(main.getPrefix() + "§cTeleporting...");
                            cancel();
                            return;
                        }
                        delay.put(becommer.getUniqueId(), delay.get(becommer.getUniqueId())-1);
                    }else{
                        becommer.sendMessage(main.getPrefix() + "§4Teleport cancelled.");
                        cancel();
                    }
                }
            }
        }.runTaskTimer(main.getInstance(), 1, 1);

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(delay.containsKey(event.getPlayer().getUniqueId())){
            if(hasMoved(event)){
                delay.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    private boolean hasMoved(PlayerMoveEvent event){
        int x = (int) event.getFrom().getX();
        int y = (int) event.getFrom().getY();
        int z = (int) event.getFrom().getZ();
        int newX = (int) event.getTo().getX();
        int newY = (int) event.getTo().getY();
        int newZ = (int) event.getTo().getZ();
        return (x != newX) || (y != newY) || (z != newZ);
    }

    private static TextComponent getClickableMessage(String message,  String hover, String command){
        TextComponent textComponent = new TextComponent(message);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        return textComponent;
    }

    private static TextComponent getAcceptMessage(Player sender){

        TextComponent textComponent = new TextComponent(main.getPrefix() + "§cuse ");
        textComponent.addExtra(getClickableMessage( "§6§n/tpaccept", "§8[§aClick to accept§8]", "/tpaccept " + sender.getName()));
        textComponent.addExtra(" §cto §caccept §cthe §cteleport §crequest.");

        return textComponent;
    }

}
