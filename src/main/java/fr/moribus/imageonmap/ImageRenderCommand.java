package fr.moribus.imageonmap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ImageRenderCommand implements CommandExecutor {
    private ImageOnMap plugin;

    public ImageRenderCommand(ImageOnMap p) {
        this.plugin = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!ImgUtility.verifyIdentity(sender)) {
            return false;
        }
        Player player = ((Player) sender);
        boolean resize = false;
        if (!player.hasPermission("imageonmap.userender")) {
            player.sendMessage("You are not allowed to use this command ( " + command.getName() + " )!");
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "You must enter image url");
            return false;
        }
        if ((args.length >= 2) && (args[1].equalsIgnoreCase("resize"))) {
            resize = true;
        }
        String url = args[0];
        TacheTraitementMap tache = new TacheTraitementMap(player, url, this.plugin, resize);
        tache.runTaskTimer(this.plugin, 0L, 10L);

        return true;
    }
}