package com.github.Emcc13;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.sql.Timestamp;
import java.util.*;

public class CraftbookPipeCreator extends JavaPlugin implements Listener, Runnable {
    private Map<UUID, Timestamp> add_player;
    private Map<UUID, Timestamp> info_player;
    private Map<UUID, Timestamp> clear_player;
    private Set<UUID> persistend_add_player;
    private Set<UUID> persistend_info_player;

    public void onEnable() {
        add_player = new HashMap<UUID, Timestamp>();
        info_player = new HashMap<UUID, Timestamp>();
        clear_player = new HashMap<UUID, Timestamp>();
        persistend_add_player = new HashSet<UUID>();
        persistend_info_player = new HashSet<UUID>();

        PipeAdd pipeAdd = new PipeAdd(this);
        PipeInfo pipeInfo = new PipeInfo(this);
        PipeClear pipeClear = new PipeClear(this);

        getCommand("pipeadd").setExecutor(pipeAdd);
        getCommand("pipeinfo").setExecutor(pipeInfo);
        getCommand("pipeclear").setExecutor(pipeClear);
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this, this, 5 * 20,
                200 * 20);
    }

    public void onDisable() {

    }

    private class PipeAdd implements CommandExecutor {
        private CraftbookPipeCreator main;

        public PipeAdd(CraftbookPipeCreator creator) {
            main = creator;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player && (((Player) sender).hasPermission("pipeCreator") || sender.isOp())) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("persist")) {
                        this.main.toggle_add_persist(((Player) sender));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bPXFL&7-&bPipe&7] verwende '/pipeadd' oder '/pipeadd persist'"));
                    }
                } else {
                    this.main.add_player.put(((Player) sender).getUniqueId(), new Timestamp(System.currentTimeMillis()));
                }
            }
            return false;
        }
    }

    private class PipeInfo implements CommandExecutor{
        private CraftbookPipeCreator main;

        public PipeInfo(CraftbookPipeCreator creator) {
            main = creator;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player && (((Player) sender).hasPermission("pipeCreator") || sender.isOp())) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("persist")) {
                        this.main.toggle_info_persist(((Player) sender));
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bPXFL&7-&bPipe&7] verwende '/pipeinfo' oder '/pipeinfo persist'"));
                    }
                } else {
                    this.main.info_player.put(((Player) sender).getUniqueId(), new Timestamp(System.currentTimeMillis()));
                }
            }
            return false;
        }
    }

    private class PipeClear implements CommandExecutor{
        private CraftbookPipeCreator main;

        public PipeClear(CraftbookPipeCreator creator) {
            main = creator;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player && (((Player) sender).hasPermission("pipeCreator") || sender.isOp())) {
//                this.main.add_player.remove(((Player) sender).getUniqueId());
//                this.main.persistend_add_player.remove(((Player) sender).getUniqueId());
                this.main.clear_player.put(((Player) sender).getUniqueId(), new Timestamp(System.currentTimeMillis()));
            }
            return false;
        }
    }

    private void toggle_add_persist(Player player) {
        UUID player_id = player.getUniqueId();
        if (this.persistend_add_player.contains(player_id)) {
            this.persistend_add_player.remove(player_id);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bPXFL&7-&bPipe&7] &3Pipe-Persist ist nun &cdeaktiviert&3!"));
        } else {
            this.persistend_add_player.add(player_id);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bPXFL&7-&bPipe&7] &3Pipe-Persist ist nun &aaktiviert&3!"));
        }
        this.add_player.remove(player_id);
    }

    private void toggle_info_persist(Player player) {
        UUID player_id = player.getUniqueId();
        if (this.persistend_info_player.contains(player_id)) {
            this.persistend_info_player.remove(player_id);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bPXFL&7-&bPipe&7] &3Pipe-Persist ist nun &cdeaktiviert&3!"));
        } else {
            this.persistend_info_player.add(player_id);
//            this.persistend_add_player.remove(player_id);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bPXFL&7-&bPipe&7] &3Pipe-Persist ist nun &aaktiviert&3!"));
        }
        this.info_player.remove(player_id);
    }

//    private class PipeAddPersist implements CommandExecutor {
//        private CraftbookPipeCreator main;
//
//        public PipeAddPersist(CraftbookPipeCreator creator) {
//            main = creator;
//        }
//
//        @Override
//        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//            if (sender instanceof Player && (((Player) sender).hasPermission("pipeCreator") || sender.isOp())) {
//                this.main.toggle_persist(((Player) sender));
//            }
//            return false;
//        }
//    }

    @Override
    public void run() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (Map.Entry<UUID, Timestamp> entry : add_player.entrySet()) {
            if (now.getTime() - entry.getValue().getTime() > 200000) {
                add_player.remove(entry.getKey());
            }
        }
        for (Map.Entry<UUID, Timestamp> entry : info_player.entrySet()) {
            if (now.getTime() - entry.getValue().getTime() > 200000) {
                info_player.remove(entry.getKey());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        this.persistend_add_player.remove(player);
        this.persistend_info_player.remove(player);
    }

    @EventHandler
    public void onPlayerChangeWord(PlayerChangedWorldEvent event) {
        UUID player = event.getPlayer().getUniqueId();
        this.persistend_add_player.remove(player);
        this.persistend_info_player.remove(player);
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        try {
            int line_idx;
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                line_idx = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                line_idx = 3;
            } else {
                return;
            }
            if (!(event.getClickedBlock().getBlockData() instanceof WallSign ||
                    event.getClickedBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign)) {
                return;
            }
            if (!((Sign) event.getClickedBlock().getState()).getLines()[1].equals("[Pipe]")) {
                return;
            }
            if (this.clear_player.containsKey(player.getUniqueId())){
                Sign s = (Sign) event.getClickedBlock().getState();
                s.setLine(line_idx, "");
                s.update();
                this.clear_player.remove(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', "&7[&bPXFL&7-&bPipe&7] &3Das Pipe Schild wurde in Zeile %LINE% geleert!"
                                .replace("%LINE%", String.valueOf(line_idx + 1))));
            }
            if (this.add_player.containsKey(player.getUniqueId()) ||
                    this.persistend_add_player.contains(player.getUniqueId())){
                String toAdd = getStringFromItem(player.getInventory().getItemInMainHand());
                Sign s = (Sign) event.getClickedBlock().getState();
                s.setLine(line_idx, s.getLine(line_idx) + ((s.getLine(line_idx).length() > 1) ? "," : "") + toAdd);
                s.update();
                this.add_player.remove(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', "&7[&bPXFL&7-&bPipe&7] &3%ITEM% wurde dem Pipe Schild in der %LINE%. Zeile hinzugefügt!"
                                .replace("%ITEM%", toAdd).replace("%LINE%", String.valueOf(line_idx + 1))));
            }
            if (this.info_player.containsKey(player.getUniqueId()) ||
                    this.persistend_info_player.contains(player.getUniqueId())) {
                String line = ((Sign) event.getClickedBlock().getState()).getLine(line_idx);
                this.info_player.remove(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes(
                        '&', "&7[&bPXFL&7-&bPipe&7] &3Das Pipe Schild enthält in Zeile %LINE% die Items: %ITEM%"
                                .replace("%ITEM%", line).replace("%LINE%", String.valueOf(line_idx + 1))));
            }
        } catch (NullPointerException ignored) {
        }
    }

    public static String getStringFromItem(ItemStack item) {
        String result = "";
        result += item.getType().name();
        if (item.getDurability() > 0) {
            result += ":" + item.getDurability();
        }

        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasEnchants()) {
                Iterator i$ = item.getItemMeta().getEnchants().entrySet().iterator();

                while (i$.hasNext()) {
                    Map.Entry<Enchantment, Integer> enchants = (Map.Entry) i$.next();
                    result += ";" + ((Enchantment) enchants.getKey()).getName() + ":" + enchants.getValue();
                }
            }

            if (item.getItemMeta().hasDisplayName()) {
                result += "|" + item.getItemMeta().getDisplayName();
            }

            String page;
            Iterator i$;
            if (item.getItemMeta().hasLore()) {
                if (!item.getItemMeta().hasDisplayName()) {
                    result += "|$IGNORE";
                }

                List<String> list = item.getItemMeta().getLore();
                i$ = list.iterator();

                while (i$.hasNext()) {
                    page = (String) i$.next();
                    result += "|" + page;
                }
            }

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof SkullMeta) {
                if (((SkullMeta) meta).hasOwner()) {
                    result += "/player:" + ((SkullMeta) meta).getOwner();
                }
            } else if (meta instanceof BookMeta) {
                if (((BookMeta) meta).hasTitle()) {
                    result += "/title:" + ((BookMeta) meta).getTitle();
                }

                if (((BookMeta) meta).hasAuthor()) {
                    result += "/author:" + ((BookMeta) meta).getAuthor();
                }

                if (((BookMeta) meta).hasPages()) {
                    i$ = ((BookMeta) meta).getPages().iterator();

                    while (i$.hasNext()) {
                        page = (String) i$.next();
                        result += "/page:" + page;
                    }
                }
            } else if (meta instanceof LeatherArmorMeta) {
                if (!((LeatherArmorMeta) meta).getColor().equals(Bukkit.getItemFactory().getDefaultLeatherColor())) {
                    result += "/color:" + ((LeatherArmorMeta) meta).getColor().getRed() + "," + ((LeatherArmorMeta) meta).getColor().getGreen() + "," + ((LeatherArmorMeta) meta).getColor().getBlue();
                }
            } else if (meta instanceof PotionMeta) {
                i$ = ((PotionMeta) meta).getCustomEffects().iterator();

                while (i$.hasNext()) {
                    PotionEffect eff = (PotionEffect) i$.next();
                    result += "/potion:" + eff.getType().getName() + ";" + eff.getDuration() + ";" + eff.getAmplifier();
                }
            } else if (meta instanceof EnchantmentStorageMeta && !((EnchantmentStorageMeta) meta).hasStoredEnchants()) {
                i$ = ((EnchantmentStorageMeta) meta).getStoredEnchants().entrySet().iterator();

                while (i$.hasNext()) {
                    Map.Entry<Enchantment, Integer> eff = (Map.Entry) i$.next();
                    result += "/enchant:" + ((Enchantment) eff.getKey()).getName() + ";" + eff.getValue();
                }
            }
        }
        return result.replace("§", "&");
    }
}
