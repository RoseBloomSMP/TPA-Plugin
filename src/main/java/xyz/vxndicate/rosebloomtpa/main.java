package xyz.vxndicate.rosebloomtpa;

import xyz.vxndicate.rosebloomtpa.manager.TeleportManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {

    @Getter
    private static main instance;

    @Getter
    private final static String prefix = "§4[§cTPA§4] §8» ";

    @Override
    public void onEnable() {
        instance = this;

        loadCommands();
        loadListener(Bukkit.getPluginManager());

        log("§aPlugin loaded.");
    }

    @Override
    public void onDisable() {
        log("§cPlugin un-loaded.");
    }

    private void loadCommands() {
        getCommand("tpa").setExecutor(new TPACommand());
        getCommand("tpaccept").setExecutor(new TPAcceptCommand());
        getCommand("tpahere").setExecutor(new TPAHereCommand());
        getCommand("tpaall").setExecutor(new TPAAllCommand());
    }

    private void loadListener(final PluginManager pluginManager) {
        pluginManager.registerEvents(new TeleportManager(), this);
    }

    private void log(final String message) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + message);
    }

}
