package su.sold.playersgeo;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GeoCommand implements CommandExecutor {
    private final Plugin plugin;
    GeoCommand(Plugin p){
        plugin = p;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sdr, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length==0){
            help(sdr,args);
            return true;
        }
        switch(args[0]){
            case "check":
                checkPlayer(sdr, args);
                break;
            case "default":
                help(sdr, args);
        }
        return true;
    }
    public void sendMessage(CommandSender sdr, String message){
        sdr.sendMessage("§c[§ePlayers§6Geo§c] §f"+message);
    }
    public void help(CommandSender sdr, String[] args){
        sendMessage(sdr,"§e/geo check - displays player's geo data.");
    }
    public void checkPlayer(CommandSender sdr, String[] args){
        if(sdr.hasPermission("playersgeo.check")){
            if(args.length<2 || args[1].length()<3){
                sendMessage(sdr, "§cEnter username.");
                return;
            }
            List<String> data = plugin.db.get(args[1]);
            if(data.size()!=0){
                sendMessage(sdr, "§f"+args[1]+ "§e from §l"+data.get(0)+", "+data.get(1));
            }else{
                sendMessage(sdr, "§eNo data for §f"+args[1]);
            }
        }else{
            sendMessage(sdr, "§cYou don't have permission.");
        }
    }
}
