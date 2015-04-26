package fr.moribus.ImageOnMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ImageRenderCommand implements CommandExecutor {
    Player player;
    boolean renderName;
    boolean imgSvg;
    ImageOnMap plugin;
    boolean resize;

    public ImageRenderCommand(ImageOnMap p) {
        this.plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!ImgUtility.verifyIdentity(sender)) {
            return false;
        }
        this.player = ((Player) sender);
        this.resize = false;
        if (!this.player.hasPermission("imageonmap.userender")) {
            this.player.sendMessage("You are not allowed to use this command ( " + command.getName() + " )!");
            return false;
        }
        if (args.length < 1) {
            this.player.sendMessage(ChatColor.RED + "You must enter image url");
            return false;
        }
        if ((args.length >= 2) && (args[1].equalsIgnoreCase("resize"))) {
            this.resize = true;
        }
        String url = args[0];
        TacheTraitementMap tache = new TacheTraitementMap(this.player, url, this.plugin, this.resize);
        tache.runTaskTimer(this.plugin, 0L, 10L);

        return true;
    }
}