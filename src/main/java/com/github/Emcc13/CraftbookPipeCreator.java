package com.github.Emcc13;

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
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.sql.Timestamp;
import java.util.*;

public class CraftbookPipeCreator extends JavaPlugin implements Listener, Runnable {
    private Map<UUID, Timestamp> activePlayer;
    private Set<UUID> persistendPlayer;

    public void onEnable() {
        activePlayer = new HashMap<UUID, Timestamp>();
        persistendPlayer = new HashSet<UUID>();

        PipeAdd pipeAdd = new PipeAdd(this);
        PipeAddPersist pipeAddPersist = new PipeAddPersist(this);

        getCommand("pipeadd").setExecutor(pipeAdd);
        getCommand("pipeadd_persist").setExecutor(pipeAddPersist);
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
                this.main.activePlayer.put(((Player) sender).getUniqueId(), new Timestamp(System.currentTimeMillis()));
            }
            return false;
        }
    }

    private class PipeAddPersist implements CommandExecutor {
        private CraftbookPipeCreator main;

        public PipeAddPersist(CraftbookPipeCreator creator) {
            main = creator;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player && (((Player) sender).hasPermission("pipeCreator") || sender.isOp())) {
                if (this.main.persistendPlayer.contains(((Player) sender).getUniqueId())){
                    this.main.persistendPlayer.remove(((Player) sender).getUniqueId());
                }else {
                    this.main.persistendPlayer.add(((Player) sender).getUniqueId());
                }
            }
            return false;
        }
    }

    @Override
    public void run() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (Map.Entry<UUID, Timestamp> entry : activePlayer.entrySet()) {
            if (now.getTime() - entry.getValue().getTime() > 200000) {
                activePlayer.remove(entry.getKey());
            }
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        try {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            if (!(event.getClickedBlock().getBlockData() instanceof WallSign ||
                    event.getClickedBlock().getBlockData() instanceof org.bukkit.block.data.type.Sign)) {
                return;
            }
            if (!(this.activePlayer.containsKey(player.getUniqueId()) || this.persistendPlayer.contains(player.getUniqueId()))) {
                return;
            }
            if (!((Sign) event.getClickedBlock().getState()).getLines()[1].equals("[Pipe]")
            ) {
                return;
            }
            String toAdd = getStringFromItem(player.getInventory().getItemInMainHand());
            Sign s = (Sign) event.getClickedBlock().getState();
            s.setLine(2, s.getLine(2) + ((s.getLine(2).length() > 1) ? "," : "") + toAdd);
            s.update();
            this.activePlayer.remove(event.getPlayer().getUniqueId());
        } catch (NullPointerException e) {
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
