package su.sold.playersgeo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GeoCommand implements CommandExecutor {
    private final Database database;
    private static final String PERMISSION = "playersgeo.check";

    public GeoCommand(Database database) {
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            help(sender);
        } else if (args[0].equals("check")) {
            checkPlayer(sender, args);
        } else {
            help(sender);
        }
        return true;
    }

    public void help(CommandSender sender) {
        sender.sendMessage(getPrefix()
                .append(Component
                        .text("/geo check - displays player's geo data.")
                        .color(NamedTextColor.YELLOW)));
    }

    public void checkPlayer(CommandSender sender, String[] args) {
        if (sender.hasPermission(PERMISSION)) {
            if (args.length < 2 || args[1].length() < 3) {
                sender.sendMessage(getPrefix()
                        .append(Component
                                .text("Enter username.")
                                .color(NamedTextColor.RED)));
                return;
            }
            List<String> data = database.get(args[1]);
            if (!data.isEmpty()) {
                sender.sendMessage(Component.text(args[1])
                        .append(Component
                                .text(" from ")
                                .color(NamedTextColor.YELLOW))
                        .append(Component
                                .text(data.get(0) + ", " + data.get(1))));
            } else {
                sender.sendMessage(Component
                        .text("No data for " + args[1])
                        .color(NamedTextColor.YELLOW));
            }
        } else {
            sender.sendMessage(getPrefix()
                    .append(Component
                            .text("You don't have permission.")
                            .color(NamedTextColor.RED)));
        }
    }

    private Component getPrefix() {
        return Component.text("[")
                .color(NamedTextColor.RED)
                .append(Component
                        .text("Players")
                        .color(NamedTextColor.YELLOW))
                .append(Component
                        .text("Geo")
                        .color(NamedTextColor.GOLD))
                .append(Component
                        .text("] ")
                        .color(NamedTextColor.RED));
    }
}
